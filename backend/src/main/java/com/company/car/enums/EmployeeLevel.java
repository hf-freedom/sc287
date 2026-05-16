package com.company.car.enums;

public enum EmployeeLevel {
    JUNIOR(1, "普通员工"),
    MIDDLE(2, "中层管理"),
    SENIOR(3, "高层管理"),
    EXECUTIVE(4, "高管");

    private final int priority;
    private final String description;

    EmployeeLevel(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }
}
