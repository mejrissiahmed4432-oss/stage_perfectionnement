package com.construction.cms.service;

import com.construction.cms.model.*;
import com.construction.cms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.construction.cms.security.services.UserDetailsImpl;

import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private StockTransactionRepository stockTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

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

    @Transactional
    public void addStock(Long materialId, Double quantity, String reason, Long projectId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        // Update current stock
        Double currentStock = material.getCurrentStock() != null ? material.getCurrentStock() : 0.0;
        material.setCurrentStock(currentStock + quantity);
        materialRepository.save(material);

        // Create transaction record
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User performedBy = null;
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            performedBy = userRepository.findById(userDetails.getId()).orElse(null);
        }

        Project project = projectId != null ? projectRepository.findById(projectId).orElse(null) : null;

        StockTransaction transaction = new StockTransaction(
            material,
            TransactionType.ENTRY,
            quantity,
            reason != null ? reason : "Stock added",
            project,
            performedBy
        );
        stockTransactionRepository.save(transaction);
    }

    @Transactional
    public void removeStock(Long materialId, Double quantity, String reason, Long projectId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        Double currentStock = material.getCurrentStock() != null ? material.getCurrentStock() : 0.0;
        if (currentStock < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + currentStock + ", Requested: " + quantity);
        }

        // Update current stock
        material.setCurrentStock(currentStock - quantity);
        materialRepository.save(material);

        // Create transaction record
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User performedBy = null;
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            performedBy = userRepository.findById(userDetails.getId()).orElse(null);
        }

        Project project = projectId != null ? projectRepository.findById(projectId).orElse(null) : null;

        StockTransaction transaction = new StockTransaction(
            material,
            TransactionType.EXIT,
            quantity,
            reason != null ? reason : "Stock removed",
            project,
            performedBy
        );
        stockTransactionRepository.save(transaction);
    }

    public List<Material> getLowStockMaterials() {
        return materialRepository.findLowStockMaterials();
    }

    public List<StockTransaction> getStockHistory(Long materialId) {
        return stockTransactionRepository.findByMaterialIdOrderByTransactionDateDesc(materialId);
    }
}
