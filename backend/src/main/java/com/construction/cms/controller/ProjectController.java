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
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('ENGINEER')")
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
    @PreAuthorize("hasRole('DIRECTOR')")
    public Project createProject(@RequestBody Project project) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return projectService.createProject(project, userDetails.getId());
    }

    @PutMapping("/{id}/assign-engineer/{engineerId}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public Project assignEngineer(@PathVariable Long id, @PathVariable Long engineerId) {
        return projectService.assignEngineer(id, engineerId);
    }

    @PutMapping("/{id}/add-employee/{employeeId}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public Project addEmployee(@PathVariable Long id, @PathVariable Long employeeId) {
        return projectService.addEmployee(id, employeeId);
    }

    @PutMapping("/{id}/add-machine/{machineId}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public Project addMachine(@PathVariable Long id, @PathVariable Long machineId) {
        return projectService.addMachine(id, machineId);
    }
}
