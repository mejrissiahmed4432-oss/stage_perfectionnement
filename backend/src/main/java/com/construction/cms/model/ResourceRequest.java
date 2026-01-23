package com.construction.cms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_requests")
public class ResourceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "engineer_id", nullable = false)
    private User engineer;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    private String details; // e.g. "Need 2 Builders" or "Need Excavator"
    
    // For precise allocation if approved
    private String resourceName; // For materials or machine typ
    private Double quantity; // For materials
    private Integer count; // For employees or machines
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime requestDate = LocalDateTime.now();

    public ResourceRequest() {}

    public ResourceRequest(Long id, Project project, User engineer, RequestType type, String details, String resourceName, Double quantity, Integer count, RequestStatus status, LocalDateTime requestDate) {
        this.id = id;
        this.project = project;
        this.engineer = engineer;
        this.type = type;
        this.details = details;
        this.resourceName = resourceName;
        this.quantity = quantity;
        this.count = count;
        this.status = status;
        this.requestDate = requestDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getEngineer() {
        return engineer;
    }

    public void setEngineer(User engineer) {
        this.engineer = engineer;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
}
