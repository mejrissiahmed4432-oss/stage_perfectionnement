package com.construction.cms.service;

import com.construction.cms.model.*;
import com.construction.cms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceRequestService {

    @Autowired
    private ResourceRequestRepository requestRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private ProjectMaterialRepository projectMaterialRepository;

    public ResourceRequest createRequest(ResourceRequest request, Long engineerId) {
        User engineer = userRepository.findById(engineerId).orElseThrow();
        request.setEngineer(engineer);
        request.setStatus(RequestStatus.PENDING);
        return requestRepository.save(request);
    }

    public List<ResourceRequest> getRequestsByProject(Long projectId) {
        return requestRepository.findByProjectId(projectId);
    }
    
    public List<ResourceRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @Transactional
    public ResourceRequest updateStatus(Long requestId, RequestStatus status) {
        ResourceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(status);
        
        if (status == RequestStatus.APPROVED) {
            fulfillRequest(request);
        }
        
        return requestRepository.save(request);
    }

    private void fulfillRequest(ResourceRequest request) {
        Project project = request.getProject();
        
        if (request.getType() == RequestType.MATERIAL) {
            // Find material by name (simple lookup) or ID if we stored ID. 
            // We stored 'resourceName'. Ideally should store ID. 
            // We'll iterate all materials to find name match for simplicity
            List<Material> materials = materialRepository.findAll(); 
            Material material = materials.stream()
                    .filter(m -> m.getName().equalsIgnoreCase(request.getResourceName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Material not found: " + request.getResourceName()));
            
            // Check stock
            if (material.getStockQuantity() < request.getQuantity()) {
                throw new RuntimeException("Not enough stock for material: " + material.getName());
            }
            
            // Deduct stock
            material.setStockQuantity(material.getStockQuantity() - request.getQuantity());
            materialRepository.save(material);
            
            // Add to assignment
            ProjectMaterial appointment = projectMaterialRepository
                    .findByProjectIdAndMaterialId(project.getId(), material.getId())
                    .orElse(new ProjectMaterial(null, project, material, 0.0));
            
            appointment.setQuantity(appointment.getQuantity() + request.getQuantity());
            projectMaterialRepository.save(appointment);
            
        } else if (request.getType() == RequestType.EMPLOYEE) {
            // "Need 2 Builders"
            int count = request.getCount();
            String professionName = request.getResourceName(); // e.g. "BUILDER" or "CAR_DRIVER"
            if (professionName == null || professionName.isBlank()) {
                throw new RuntimeException("Profession is required for employee request");
            }
            Profession profession;
            try {
                profession = Profession.valueOf(professionName.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid profession: " + professionName);
            }
            List<Employee> available = employeeRepository.findByProfessionAndStatus(profession, EmployeeStatus.AVAILABLE);
            
            if (available.size() < count) {
                throw new RuntimeException("Not enough available employees with profession: " + professionName);
            }
            
            for (int i = 0; i < count; i++) {
                Employee e = available.get(i);
                e.setStatus(EmployeeStatus.ASSIGNED);
                employeeRepository.save(e);
                project.getAssignedEmployees().add(e);
            }
            projectRepository.save(project);
            
        } else if (request.getType() == RequestType.MACHINE) {
            // "Need 1 Crane"
            int count = request.getCount();
            String type = request.getResourceName(); // "Crane"
            
            // We need a findByTypeAndStatus in repo. 
            // Or just filter all Available machines.
            List<Machine> available = machineRepository.findByStatus(MachineStatus.AVAILABLE).stream()
                    .filter(m -> m.getType().equalsIgnoreCase(type))
                    .toList();
            
            if (available.size() < count) {
                throw new RuntimeException("Not enough available machines of type: " + type);
            }
            
            for (int i = 0; i < count; i++) {
                Machine m = available.get(i);
                m.setStatus(MachineStatus.ASSIGNED);
                machineRepository.save(m);
                project.getAssignedMachines().add(m);
            }
            projectRepository.save(project);
        }
    }
}
