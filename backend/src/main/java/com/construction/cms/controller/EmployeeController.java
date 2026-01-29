package com.construction.cms.controller;

import com.construction.cms.model.Employee;
import com.construction.cms.model.Profession;
import com.construction.cms.service.EmployeeService;
import com.construction.cms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public List<Employee> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String profession) {
        if (name != null && !name.isBlank()) {
            return employeeService.searchByName(name);
        } else if (profession != null && !profession.isBlank()) {
            try {
                Profession p = Profession.valueOf(profession.trim().toUpperCase());
                return employeeService.searchByProfession(p);
            } catch (IllegalArgumentException ex) {
                // Invalid profession filter -> return empty list instead of 500
                return List.of();
            }
        }
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody Employee employee) {
        // Check if email already exists
        if (employee.getEmail() != null && !employee.getEmail().isEmpty()) {
            if (employeeRepository.existsByEmail(employee.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("field", "email", "message", "Email is already in use!"));
            }
        }
        // Check if phone already exists
        if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
            if (employeeRepository.existsByPhone(employee.getPhone())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("field", "phone", "message", "Phone number is already in use!"));
            }
        }
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        // Check if email already exists for another employee
        if (employee.getEmail() != null && !employee.getEmail().isEmpty()) {
            Optional<Employee> existingWithEmail = employeeRepository.findByEmail(employee.getEmail());
            if (existingWithEmail.isPresent() && !existingWithEmail.get().getId().equals(id)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("field", "email", "message", "Email is already in use by another employee!"));
            }
        }
        // Check if phone already exists for another employee
        if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
            Optional<Employee> existingWithPhone = employeeRepository.findByPhone(employee.getPhone());
            if (existingWithPhone.isPresent() && !existingWithPhone.get().getId().equals(id)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("field", "phone", "message", "Phone number is already in use by another employee!"));
            }
        }
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }
}
