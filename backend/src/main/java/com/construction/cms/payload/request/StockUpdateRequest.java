package com.construction.cms.payload.request;

public class StockUpdateRequest {
    private Double quantity;
    private String reason;
    private Long projectId; // optional, for project-related transactions

    public StockUpdateRequest() {}

    public StockUpdateRequest(Double quantity, String reason, Long projectId) {
        this.quantity = quantity;
        this.reason = reason;
        this.projectId = projectId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
