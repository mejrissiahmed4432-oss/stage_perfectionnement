package com.construction.cms.payload.request;

public class ProgressUpdateRequest {
    private Double progressPercentage;
    private String currentPhase;

    public ProgressUpdateRequest() {}

    public ProgressUpdateRequest(Double progressPercentage, String currentPhase) {
        this.progressPercentage = progressPercentage;
        this.currentPhase = currentPhase;
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
}
