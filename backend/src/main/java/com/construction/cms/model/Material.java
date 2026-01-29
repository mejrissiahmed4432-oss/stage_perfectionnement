package com.construction.cms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // Stock Management - using currentStock as the main field
    private Double currentStock = 0.0;
    private Double minStockLevel = 0.0;
    private String unit; // e.g., "kg", "m3", "pieces", "bags", "liters"

    public Material() {}

    public Material(Long id, String name, String description, Double currentStock, String unit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.currentStock = currentStock;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Double currentStock) {
        this.currentStock = currentStock;
    }

    public Double getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Double minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Backward compatibility - map to currentStock
    public Double getStockQuantity() {
        return currentStock;
    }

    public void setStockQuantity(Double stockQuantity) {
        this.currentStock = stockQuantity;
    }

    // Computed method to check if stock is low
    public boolean isLowStock() {
        return currentStock != null && minStockLevel != null && currentStock <= minStockLevel;
    }
}
