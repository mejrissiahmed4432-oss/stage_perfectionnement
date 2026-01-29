package com.construction.cms.controller;

import com.construction.cms.model.*;
import com.construction.cms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private MaterialRepository materialRepository;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('ADMIN')")
    public Map<String, Object> getSummary() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalProjects = projectRepository.count();
        long completedProjects = projectRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count();
        long delayedProjects = projectRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProjectStatus.DELAYED).count();
        
        long totalEmployees = employeeRepository.count();
        long availableEmployees = employeeRepository.findByStatus(EmployeeStatus.AVAILABLE).size();
        
        long totalMachines = machineRepository.count();
        long availableMachines = machineRepository.findByStatus(MachineStatus.AVAILABLE).size();
        
        stats.put("totalProjects", totalProjects);
        stats.put("completedProjects", completedProjects);
        stats.put("delayedProjects", delayedProjects);
        stats.put("totalEmployees", totalEmployees);
        stats.put("availableEmployees", availableEmployees);
        stats.put("totalMachines", totalMachines);
        stats.put("availableMachines", availableMachines);
        
        return stats;
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('ADMIN') or hasRole('ENGINEER')")
    public List<Material> getLowStockMaterials() {
        // Simple threshold < 50
        return materialRepository.findAll().stream()
                .filter(m -> m.getStockQuantity() < 50)
                .collect(Collectors.toList());
    }
}
