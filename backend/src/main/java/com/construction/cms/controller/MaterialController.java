package com.construction.cms.controller;

import com.construction.cms.model.Material;
import com.construction.cms.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or hasRole('ENGINEER')")
    public List<Material> getAllMaterials() {
        return materialService.getAllMaterials();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Material createMaterial(@RequestBody Material material) {
        return materialService.createMaterial(material);
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public Material updateStock(@PathVariable Long id, @RequestParam Double quantity) {
        return materialService.updateStock(id, quantity);
    }

    @PostMapping("/{id}/stock/add")
    @PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<?> addStock(@PathVariable Long id, @RequestBody com.construction.cms.payload.request.StockUpdateRequest request) {
        materialService.addStock(id, request.getQuantity(), request.getReason(), request.getProjectId());
        return org.springframework.http.ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stock/remove")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public org.springframework.http.ResponseEntity<?> removeStock(@PathVariable Long id, @RequestBody com.construction.cms.payload.request.StockUpdateRequest request) {
        materialService.removeStock(id, request.getQuantity(), request.getReason(), request.getProjectId());
        return org.springframework.http.ResponseEntity.ok().build();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public org.springframework.http.ResponseEntity<?> getLowStockMaterials() {
        return org.springframework.http.ResponseEntity.ok(materialService.getLowStockMaterials());
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public org.springframework.http.ResponseEntity<?> getStockHistory(@PathVariable Long id) {
        return org.springframework.http.ResponseEntity.ok(materialService.getStockHistory(id));
    }
}
