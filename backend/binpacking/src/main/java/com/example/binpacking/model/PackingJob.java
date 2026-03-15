package com.example.binpacking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "packing_job")
public class PackingJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "container_l", nullable = false)
    private Double containerL;

    @Column(name = "container_w", nullable = false)
    private Double containerW;

    @Column(name = "container_h", nullable = false)
    private Double containerH;

    private Double utilization;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PackingItem> items = new HashSet<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PlacementResult> placements = new HashSet<>();

    // Getters and setters
    public UUID getId() { return id; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Double getContainerL() { return containerL; }
    public void setContainerL(Double containerL) { this.containerL = containerL; }
    public Double getContainerW() { return containerW; }
    public void setContainerW(Double containerW) { this.containerW = containerW; }
    public Double getContainerH() { return containerH; }
    public void setContainerH(Double containerH) { this.containerH = containerH; }
    public Double getUtilization() { return utilization; }
    public void setUtilization(Double utilization) { this.utilization = utilization; }
    public Set<PackingItem> getItems() { return items; }
    public Set<PlacementResult> getPlacements() { return placements; }
}