package com.company.car.entity;

import lombok.Data;

@Data
public class Driver {
    private String id;
    private String name;
    private String phone;
    private String licenseNumber;
    private boolean available;
}
