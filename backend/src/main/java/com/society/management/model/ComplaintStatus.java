package com.society.management.model;

public enum ComplaintStatus {
    PENDING("Pending"),
    RESOLVED("Resolved"),
    REJECTED("Rejected");

    private final String displayName;

    ComplaintStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}