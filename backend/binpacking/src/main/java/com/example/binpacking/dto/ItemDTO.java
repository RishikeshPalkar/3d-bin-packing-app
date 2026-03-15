package com.example.binpacking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ItemDTO {
    private String name;

    @Positive(message = "Item length must be > 0")
    private Double length;

    @Positive(message = "Item width must be > 0")
    private Double width;

    @Positive(message = "Item height must be > 0")
    private Double height;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }
    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
}