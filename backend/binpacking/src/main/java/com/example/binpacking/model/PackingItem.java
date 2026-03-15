package com.example.binpacking.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "packing_item")
public class PackingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private PackingJob job;

    private String name;

    @Column(nullable = false)
    private Double length;

    @Column(nullable = false)
    private Double width;

    @Column(nullable = false)
    private Double height;

    // Getters and setters
    public UUID getId() { return id; }
    public PackingJob getJob() { return job; }
    public void setJob(PackingJob job) { this.job = job; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }
    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
}