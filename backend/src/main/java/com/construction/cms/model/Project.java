package com.construction.cms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    private LocalDate startDate;
    private LocalDate endDate;

    private Double budget;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.PLANNED;

    // Director who created/manages it
    @ManyToOne
    @JoinColumn(name = "director_id")
    private User director;

    // Engineer responsible
    @ManyToOne
    @JoinColumn(name = "engineer_id")
    private User engineer;

    // Assigned Resources
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectMaterial> materials;

    @ManyToMany
    @JoinTable(
        name = "project_employees",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> assignedEmployees;
    
    // Machines assigned
    @OneToMany
    @JoinTable(
        name = "project_machines",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "machine_id")
    )
    private List<Machine> assignedMachines;

    // Progress Tracking
    private Double progressPercentage = 0.0;
    private String currentPhase; // e.g., "Foundation", "Structure", "Finishing"
    private LocalDate lastProgressUpdate;

    public Project() {}

    public Project(Long id, String name, String location, LocalDate startDate, LocalDate endDate, Double budget, ProjectStatus status, User director, User engineer) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.status = status;
        this.director = director;
        this.engineer = engineer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public User getDirector() {
        return director;
    }

    public void setDirector(User director) {
        this.director = director;
    }

    @JsonIgnore
    public User getEngineer() {
        return engineer;
    }

    public void setEngineer(User engineer) {
        this.engineer = engineer;
    }

    @JsonIgnore
    public List<ProjectMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<ProjectMaterial> materials) {
        this.materials = materials;
    }

    public List<Employee> getAssignedEmployees() {
        return assignedEmployees;
    }

    public void setAssignedEmployees(List<Employee> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }

    @JsonIgnore
    public List<Machine> getAssignedMachines() {
        return assignedMachines;
    }

    public void setAssignedMachines(List<Machine> assignedMachines) {
        this.assignedMachines = assignedMachines;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public LocalDate getLastProgressUpdate() {
        return lastProgressUpdate;
    }

    public void setLastProgressUpdate(LocalDate lastProgressUpdate) {
        this.lastProgressUpdate = lastProgressUpdate;
    }

    // Computed method to check if project is delayed
    public boolean isDelayed() {
        if (endDate == null || status == ProjectStatus.COMPLETED) {
            return false;
        }
        return LocalDate.now().isAfter(endDate);
    }
}
