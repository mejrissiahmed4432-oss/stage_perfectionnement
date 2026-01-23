package com.construction.cms.service;

import com.construction.cms.model.Employee;
import com.construction.cms.model.EmployeeStatus;
import com.construction.cms.model.Machine;
import com.construction.cms.model.MachineStatus;
import com.construction.cms.repository.EmployeeRepository;
import com.construction.cms.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }

    public Optional<Machine> getMachineById(Long id) {
        return machineRepository.findById(id);
    }

    public Machine createMachine(Machine machine) {
        // By default, no driver
        machine.setStatus(MachineStatus.AVAILABLE);
        return machineRepository.save(machine);
    }

    public Machine updateMachine(Long id, Machine machineDetails) {
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Machine not found with id: " + id));

        machine.setType(machineDetails.getType());
        machine.setSerialNumber(machineDetails.getSerialNumber());
        machine.setStatus(machineDetails.getStatus());
        
        // Driver assignment handled separately or carefully here
        return machineRepository.save(machine);
    }

    public void deleteMachine(Long id) {
        machineRepository.deleteById(id);
    }

    @Transactional
    public Machine assignDriver(Long machineId, Long driverId) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        Employee driver = employeeRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (!"Driver".equalsIgnoreCase(driver.getProfession())) {
            throw new RuntimeException("Employee is not a Driver");
        }

        if (driver.getStatus() != EmployeeStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available");
        }

        // Unassign previous driver if any? For now assume machine must be free of driver??
        // Or if simple replacement:
        
        machine.setDriver(driver);
        driver.setStatus(EmployeeStatus.ASSIGNED);
        
        // Machine status could be considered ASSIGNED if it has a project, 
        // but driver assignment is one part. 
        // Let's assume assigning a driver makes the machine ready or it might still need a project.
        // Prompt says "Machine cannot operate without a driver".
        
        employeeRepository.save(driver);
        return machineRepository.save(machine);
    }
}
