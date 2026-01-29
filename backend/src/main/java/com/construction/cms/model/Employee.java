package com.construction.cms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 5, message = "Full name must be at least 5 characters long")
    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Profession is required")
    @Column(nullable = false)
    private Profession profession;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;
    
    @Pattern(regexp = "^\\d{8}$", message = "Phone number must be exactly 8 digits")
    @Column(unique = true)
    private String phone;
    
    private String address;
    
    @Min(value = 700, message = "Salary must be at least 700")
    private Double salary = 700.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status = EmployeeStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "current_project_id")
    @JsonIgnoreProperties({"assignedEmployees", "materials", "assignedMachines", "director", "engineer"})
    private Project currentProject;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    private LocalDate startDate;

    private LocalDate endDate;
    
    public Employee() {}

    public Employee(Long id, String fullName, Profession profession, String email, String phone, EmployeeStatus status, Double salary, Project currentProject, ContractType contractType, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.fullName = fullName;
        this.profession = profession;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.salary = salary;
        this.currentProject = currentProject;
        this.contractType = contractType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
