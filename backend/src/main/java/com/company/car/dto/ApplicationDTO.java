package com.company.car.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationDTO {
    private String employeeId;
    private String purpose;
    private String destination;
    private int passengers;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean emergency;
}
