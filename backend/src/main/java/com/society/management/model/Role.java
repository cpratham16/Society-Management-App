package com.society.management.model;

public enum Role {
    ADMIN("Admin"),
    MEMBER("Member"),
    GUEST("Guest");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}