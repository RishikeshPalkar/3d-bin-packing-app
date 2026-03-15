package com.example.binpacking.service;

import com.example.binpacking.dto.*;
import com.example.binpacking.exception.JobNotFoundException;
import com.example.binpacking.model.*;
import com.example.binpacking.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PackingService {

    private final PackingRepository jobRepo;
    private final PackingItemRepository itemRepo;
    private final PlacementResultRepository placementRepo;
    private final WebClient webClient;

    @PersistenceContext
    private EntityManager entityManager;

    public PackingService(PackingRepository jobRepo,
                          PackingItemRepository itemRepo,
                          PlacementResultRepository placementRepo,
                          @Value("${ml.service.url}") String mlServiceUrl) {
        this.jobRepo = jobRepo;
        this.itemRepo = itemRepo;
        this.placementRepo = placementRepo;
        this.webClient = WebClient.builder()
                .baseUrl(mlServiceUrl)
                .build();
    }

    @Transactional
    public PackingResponseDTO createAndPack(PackingRequestDTO request) {
        PackingJob job = new PackingJob();
        job.setContainerL(request.getContainerL());
        job.setContainerW(request.getContainerW());
        job.setContainerH(request.getContainerH());
        job.setStatus(JobStatus.PROCESSING);
        job = jobRepo.save(job);

        List<PackingItem> savedItems = new ArrayList<>();
        for (ItemDTO dto : request.getItems()) {
            PackingItem item = new PackingItem();
            item.setJob(job);
            item.setName(dto.getName());
            item.setLength(dto.getLength());
            item.setWidth(dto.getWidth());
            item.setHeight(dto.getHeight());
            savedItems.add(itemRepo.save(item));
        }

        itemRepo.flush();
        jobRepo.flush();

        try {
            Map<String, Object> mlResult = callMLService(job, savedItems);
            saveResults(job, savedItems, mlResult);
            job.setStatus(JobStatus.DONE);
        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            jobRepo.save(job);
            throw new RuntimeException("ML service error: " + e.getMessage(), e);
        }

        jobRepo.save(job);
        placementRepo.flush();                    // ← flush placements to DB

        entityManager.clear();
        PackingJob fresh = jobRepo.findByIdWithPlacements(job.getId()).orElseThrow();
        return toResponseDTO(fresh);
    }

    private Map<String, Object> callMLService(PackingJob job, List<PackingItem> items) {
        List<Map<String, Object>> itemsPayload = items.stream()
                .map(i -> Map.<String, Object>of(
                        "id", i.getId().toString(),
                        "length", i.getLength(),
                        "width", i.getWidth(),
                        "height", i.getHeight()
                ))
                .collect(Collectors.toList());

        Map<String, Object> payload = Map.of(
                "container", Map.of(
                        "length", job.getContainerL(),
                        "width", job.getContainerW(),
                        "height", job.getContainerH()
                ),
                "items", itemsPayload
        );

        return webClient.post()
                .uri("/predict")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(java.time.Duration.ofSeconds(60))
                .block();
    }

    @SuppressWarnings("unchecked")
    private void saveResults(PackingJob job, List<PackingItem> items, Map<String, Object> mlResult) {
        Map<String, PackingItem> itemMap = items.stream()
                .collect(Collectors.toMap(i -> i.getId().toString(), i -> i));

        List<Map<String, Object>> placements =
                (List<Map<String, Object>>) mlResult.get("placements");

        double totalVolume = job.getContainerL() * job.getContainerW() * job.getContainerH();
        double packedVolume = 0.0;

        for (Map<String, Object> p : placements) {
            String itemId = (String) p.get("item_id");
            PackingItem item = itemMap.get(itemId);
            if (item == null) continue;

            PlacementResult result = new PlacementResult();
            result.setJob(job);
            result.setItem(item);
            result.setX(((Number) p.get("x")).doubleValue());
            result.setY(((Number) p.get("y")).doubleValue());
            result.setZ(((Number) p.get("z")).doubleValue());
            result.setPlaced((Boolean) p.getOrDefault("placed", true));
            result.setRotation((String) p.getOrDefault("rotation", "NONE"));
            placementRepo.save(result);

            if (result.getPlaced()) {
                packedVolume += item.getLength() * item.getWidth() * item.getHeight();
            }
        }

        job.setUtilization(totalVolume > 0 ? (packedVolume / totalVolume) * 100.0 : 0.0);
    }

    @Transactional(readOnly = true)
    public PackingResponseDTO getJob(UUID id) {
        PackingJob job = jobRepo.findByIdWithPlacements(id)
                .orElseThrow(() -> new JobNotFoundException(id));
        return toResponseDTO(job);
    }

    @Transactional(readOnly = true)
    public List<PackingResponseDTO> getAllJobs() {
        return jobRepo.findAll().stream()
                .map(j -> jobRepo.findByIdWithPlacements(j.getId()).orElseThrow())
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteJob(UUID id) {
        if (!jobRepo.existsById(id)) throw new JobNotFoundException(id);
        jobRepo.deleteById(id);
    }

    private PackingResponseDTO toResponseDTO(PackingJob job) {
        PackingResponseDTO dto = new PackingResponseDTO();
        dto.setJobId(job.getId());
        dto.setStatus(job.getStatus());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setContainerL(job.getContainerL());
        dto.setContainerW(job.getContainerW());
        dto.setContainerH(job.getContainerH());
        dto.setUtilization(job.getUtilization());

        List<PlacementDTO> placements = job.getPlacements().stream().map(p -> {
            PlacementDTO pdto = new PlacementDTO();
            pdto.setItemId(p.getItem().getId());
            pdto.setItemName(p.getItem().getName());
            pdto.setX(p.getX());
            pdto.setY(p.getY());
            pdto.setZ(p.getZ());
            pdto.setPlaced(p.getPlaced());
            pdto.setRotation(p.getRotation());
            return pdto;
        }).collect(Collectors.toList());

        dto.setPlacements(placements);
        return dto;
    }
}