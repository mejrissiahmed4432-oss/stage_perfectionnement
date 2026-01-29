package com.construction.cms.controller;

import com.construction.cms.model.Project;
import com.construction.cms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.construction.cms.security.services.UserDetailsImpl;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or hasRole('ENGINEER')")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('ENGINEER')")
    public List<Project> getAssignedProjects() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return projectService.getProjectsByEngineer(userDetails.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public Project createProject(@RequestBody Project project) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return projectService.createProject(project, userDetails.getId());
    }

    @PutMapping("/{id}/assign-engineer/{engineerId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public Project assignEngineer(@PathVariable Long id, @PathVariable Long engineerId) {
        return projectService.assignEngineer(id, engineerId);
    }

    @PutMapping("/{id}/add-employee/{employeeId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public Project addEmployee(@PathVariable Long id, @PathVariable Long employeeId) {
        return projectService.addEmployee(id, employeeId);
    }

    @PutMapping("/{id}/add-machine/{machineId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public Project addMachine(@PathVariable Long id, @PathVariable Long machineId) {
        return projectService.addMachine(id, machineId);
    }

    @PutMapping("/{id}/progress")
    @PreAuthorize("hasRole('ENGINEER') or hasRole('PROJECT_MANAGER')")
    public ResponseEntity<?> updateProgress(@PathVariable Long id, @RequestBody com.construction.cms.payload.request.ProgressUpdateRequest request) {
        Project project = projectService.updateProgress(id, request.getProgressPercentage(), request.getCurrentPhase());
        return ResponseEntity.ok(project);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
}
