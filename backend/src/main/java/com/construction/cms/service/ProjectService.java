package com.construction.cms.service;

import com.construction.cms.model.*;
import com.construction.cms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByDirector(Long directorId) {
        return projectRepository.findByDirectorId(directorId);
    }
    
    public List<Project> getProjectsByEngineer(Long engineerId) {
        return projectRepository.findByEngineerId(engineerId);
    }

    public Project createProject(Project project, Long directorId) {
        User director = userRepository.findById(directorId)
                .orElseThrow(() -> new RuntimeException("Director not found"));
        project.setDirector(director);
        return projectRepository.save(project);
    }

    public Project assignEngineer(Long projectId, Long engineerId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() -> new RuntimeException("Engineer not found"));
        
        project.setEngineer(engineer);
        return projectRepository.save(project);
    }

    @Transactional
    public Project addEmployee(Long projectId, Long employeeId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        
        if (employee.getStatus() != EmployeeStatus.AVAILABLE) {
            throw new RuntimeException("Employee not available");
        }
        
        employee.setStatus(EmployeeStatus.ASSIGNED);
        employeeRepository.save(employee);
        
        project.getAssignedEmployees().add(employee);
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project addMachine(Long projectId, Long machineId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Machine machine = machineRepository.findById(machineId).orElseThrow();
        
        if (machine.getStatus() != MachineStatus.AVAILABLE) {
            throw new RuntimeException("Machine not available");
        }
        
        machine.setStatus(MachineStatus.ASSIGNED);
        machineRepository.save(machine);
        
        project.getAssignedMachines().add(machine);
        return projectRepository.save(project);
    }
}
