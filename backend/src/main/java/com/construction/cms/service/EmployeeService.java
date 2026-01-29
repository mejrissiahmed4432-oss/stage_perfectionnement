package com.construction.cms.service;

import com.construction.cms.model.Employee;
import com.construction.cms.model.EmployeeStatus;
import com.construction.cms.model.Profession;
import com.construction.cms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    @Lazy
    private EmployeeService self;

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        LocalDate today = LocalDate.now();
        List<Employee> all = employeeRepository.findAllWithCurrentProject();
        List<Employee> result = new ArrayList<>(all.size());
        for (Employee e : all) {
            applyContractEndInMemory(e, today);
            result.add(e);
        }
        try {
            self.persistExpiredContracts(today);
        } catch (Exception ex) {
            log.warn("Could not persist expired contract statuses (list still returned): {}", ex.getMessage());
        }
        return result;
    }

    /** Updates status to CONTRACT_END in memory when endDate is today or in the past. No save. */
    private void applyContractEndInMemory(Employee employee, LocalDate today) {
        if (employee.getEndDate() != null && !employee.getEndDate().isAfter(today)) {
            if (employee.getStatus() != EmployeeStatus.CONTRACT_END) {
                employee.setStatus(EmployeeStatus.CONTRACT_END);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistExpiredContracts(LocalDate today) {
        try {
            List<Employee> expired = employeeRepository.findExpiredContractsToUpdate(today, EmployeeStatus.CONTRACT_END);
            for (Employee e : expired) {
                e.setStatus(EmployeeStatus.CONTRACT_END);
                employeeRepository.save(e);
            }
        } catch (Exception ex) {
            log.warn("Failed to persist expired contract statuses: {}", ex.getMessage());
        }
    }

    private Employee processStatus(Employee employee) {
        LocalDate today = LocalDate.now();
        if (employee.getEndDate() != null && !employee.getEndDate().isAfter(today)) {
            if (employee.getStatus() != EmployeeStatus.CONTRACT_END) {
                employee.setStatus(EmployeeStatus.CONTRACT_END);
                return employeeRepository.save(employee);
            }
        }
        return employee;
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setFullName(employeeDetails.getFullName());
        employee.setProfession(employeeDetails.getProfession());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhone(employeeDetails.getPhone());
        employee.setAddress(employeeDetails.getAddress());
        employee.setStatus(employeeDetails.getStatus());
        employee.setSalary(employeeDetails.getSalary());
        employee.setCurrentProject(employeeDetails.getCurrentProject());
        employee.setContractType(employeeDetails.getContractType());
        employee.setStartDate(employeeDetails.getStartDate());
        employee.setEndDate(employeeDetails.getEndDate());

        Employee updatedEmployee = employeeRepository.save(employee);
        return checkContractExpiry(updatedEmployee);
    }

    private Employee checkContractExpiry(Employee employee) {
        return processStatus(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> getEmployeesByProfession(Profession profession) {
        return processStatusList(employeeRepository.findByProfessionWithCurrentProject(profession));
    }

    public List<Employee> searchByName(String name) {
        return processStatusList(employeeRepository.findByFullNameContainingIgnoreCaseWithCurrentProject(name));
    }

    public List<Employee> searchByProfession(Profession profession) {
        return processStatusList(employeeRepository.findByProfessionWithCurrentProject(profession));
    }

    private List<Employee> processStatusList(List<Employee> list) {
        LocalDate today = LocalDate.now();
        List<Employee> result = new ArrayList<>(list.size());
        for (Employee e : list) {
            applyContractEndInMemory(e, today);
            result.add(e);
        }
        try {
            self.persistExpiredContracts(today);
        } catch (Exception ex) {
            log.warn("Could not persist expired contract statuses: {}", ex.getMessage());
        }
        return result;
    }
}
