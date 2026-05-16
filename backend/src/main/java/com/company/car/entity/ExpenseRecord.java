package com.company.car.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseRecord {
    private String id;
    private String applicationId;
    private String carId;
    private String carPlateNumber;
    private double startMileage;
    private double endMileage;
    private double distance;
    private double fuelUsed;
    private double fuelCost;
    private double totalCost;
    private LocalDateTime createTime;
}
