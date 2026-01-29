package com.construction.cms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // ENTRY, EXIT
    
    private Double quantity;
    private String reason; // e.g., "Purchase", "Project Allocation"
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project; // null if not project-related
    
    private LocalDateTime transactionDate = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User performedBy;

    public StockTransaction() {}

    public StockTransaction(Material material, TransactionType type, Double quantity, String reason, Project project, User performedBy) {
        this.material = material;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.project = project;
        this.performedBy = performedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }
}
