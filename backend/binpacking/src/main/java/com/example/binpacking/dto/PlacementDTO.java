package com.example.binpacking.dto;

import java.util.UUID;

public class PlacementDTO {
    private UUID itemId;
    private String itemName;
    private Double x, y, z;
    private Boolean placed;
    private String rotation;

    public UUID getItemId() { return itemId; }
    public void setItemId(UUID itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
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