package com.construction.cms.repository;

import com.construction.cms.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByDirectorId(Long directorId);
    List<Project> findByEngineerId(Long engineerId);
}
