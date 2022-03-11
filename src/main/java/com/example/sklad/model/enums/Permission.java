package com.example.sklad.model.enums;

public enum Permission {
    READ("read"),
    WRITE("write"),
    MODERATE("moderate");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
