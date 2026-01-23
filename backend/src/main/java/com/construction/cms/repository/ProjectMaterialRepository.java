package com.construction.cms.repository;

import com.construction.cms.model.ProjectMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectMaterialRepository extends JpaRepository<ProjectMaterial, Long> {
    Optional<ProjectMaterial> findByProjectIdAndMaterialId(Long projectId, Long materialId);
}
