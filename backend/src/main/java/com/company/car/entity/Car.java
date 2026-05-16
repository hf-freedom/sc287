package com.company.car.entity;

import com.company.car.enums.CarStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Car {
    private String id;
    private String plateNumber;
    private String brand;
    private String model;
    private int seats;
    private CarStatus status;
    private double currentMileage;
    private double fuelConsumption;
    private LocalDate lastMaintenanceDate;
    private int maintenanceIntervalKm;
}
