package com.example.binpacking.dto;

import com.example.binpacking.model.JobStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PackingResponseDTO {
    private UUID jobId;
    private JobStatus status;
    private LocalDateTime createdAt;
    private Double containerL, containerW, containerH;
    private Double utilization;
    private List<PlacementDTO> placements;

    public UUID getJobId() { return jobId; }
    public void setJobId(UUID jobId) { this.jobId = jobId; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Double getContainerL() { return containerL; }
    public void setContainerL(Double containerL) { this.containerL = containerL; }
    public Double getContainerW() { return containerW; }
    public void setContainerW(Double containerW) { this.containerW = containerW; }
    public Double getContainerH() { return containerH; }
    public void setContainerH(Double containerH) { this.containerH = containerH; }
    public Double getUtilization() { return utilization; }
    public void setUtilization(Double utilization) { this.utilization = utilization; }
    public List<PlacementDTO> getPlacements() { return placements; }
    public void setPlacements(List<PlacementDTO> placements) { this.placements = placements; }
}