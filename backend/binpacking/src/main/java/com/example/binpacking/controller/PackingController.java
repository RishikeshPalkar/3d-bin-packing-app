package com.example.binpacking.controller;

import com.example.binpacking.dto.PackingRequestDTO;
import com.example.binpacking.dto.PackingResponseDTO;
import com.example.binpacking.service.PackingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class PackingController {

    private final PackingService packingService;

    public PackingController(PackingService packingService) {
        this.packingService = packingService;
    }

    @PostMapping("/pack")
    public ResponseEntity<PackingResponseDTO> pack(@Valid @RequestBody PackingRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(packingService.createAndPack(request));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<PackingResponseDTO>> getAllJobs() {
        return ResponseEntity.ok(packingService.getAllJobs());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<PackingResponseDTO> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(packingService.getJob(id));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        packingService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}