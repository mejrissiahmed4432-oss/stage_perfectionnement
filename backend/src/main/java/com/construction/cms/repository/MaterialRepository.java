package com.construction.cms.repository;

import com.construction.cms.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    @Query("SELECT m FROM Material m WHERE m.currentStock <= m.minStockLevel")
    List<Material> findLowStockMaterials();
}
