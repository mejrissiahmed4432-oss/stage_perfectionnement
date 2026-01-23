package com.construction.cms.service;

import com.construction.cms.model.Material;
import com.construction.cms.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public Material createMaterial(Material material) {
        return materialRepository.save(material);
    }

    public Material updateStock(Long id, Double quantity) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        
        // This is a simple update, could be + or -
        material.setStockQuantity(quantity);
        return materialRepository.save(material);
    }
}
