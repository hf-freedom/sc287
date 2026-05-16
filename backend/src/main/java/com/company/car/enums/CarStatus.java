package com.company.car.enums;

public enum CarStatus {
    AVAILABLE("可用"),
    IN_USE("使用中"),
    MAINTENANCE("保养中"),
    RESERVED("已预约");

    private final String description;

    CarStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
