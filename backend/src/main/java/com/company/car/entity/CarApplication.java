package com.company.car.entity;

import com.company.car.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarApplication {
    private String id;
    private String employeeId;
    private String employeeName;
    private int employeePriority;
    private String purpose;
    private String destination;
    private int passengers;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String carId;
    private String carPlateNumber;
    private String driverId;
    private String driverName;
    private ApplicationStatus status;
    private boolean emergency;
    private LocalDateTime createTime;
    private LocalDateTime approveTime;
    private String approveRemark;
    private Double endMileage;
    private Double fuelUsed;
}
