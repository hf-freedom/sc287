package com.company.car.dto;

import lombok.Data;

@Data
public class ReturnCarDTO {
    private String applicationId;
    private double endMileage;
    private double fuelUsed;
}
