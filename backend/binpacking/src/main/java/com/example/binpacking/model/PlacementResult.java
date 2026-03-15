package com.example.binpacking.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "placement_result")
public class PlacementResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private PackingJob job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private PackingItem item;

    @Column(nullable = false)
    private Double x = 0.0;

    @Column(nullable = false)
    private Double y = 0.0;

    @Column(nullable = false)
    private Double z = 0.0;

    @Column(nullable = false)
    private Boolean placed = false;

    private String rotation;

    // Getters and setters
    public UUID getId() { return id; }
    public PackingJob getJob() { return job; }
    public void setJob(PackingJob job) { this.job = job; }
    public PackingItem getItem() { return item; }
    public void setItem(PackingItem item) { this.item = item; }
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public Double getZ() { return z; }
    public void setZ(Double z) { this.z = z; }
    public Boolean getPlaced() { return placed; }
    public void setPlaced(Boolean placed) { this.placed = placed; }
    public String getRotation() { return rotation; }
    public void setRotation(String rotation) { this.rotation = rotation; }
}