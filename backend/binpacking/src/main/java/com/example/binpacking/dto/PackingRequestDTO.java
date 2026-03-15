package com.example.binpacking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class PackingRequestDTO {

    @Positive(message = "Container length must be > 0")
    private Double containerL;

    @Positive(message = "Container width must be > 0")
    private Double containerW;

    @Positive(message = "Container height must be > 0")
    private Double containerH;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<ItemDTO> items;

    public Double getContainerL() { return containerL; }
    public void setContainerL(Double containerL) { this.containerL = containerL; }
    public Double getContainerW() { return containerW; }
    public void setContainerW(Double containerW) { this.containerW = containerW; }
    public Double getContainerH() { return containerH; }
    public void setContainerH(Double containerH) { this.containerH = containerH; }
    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }
}