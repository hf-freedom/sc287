package com.company.car.entity;

import com.company.car.enums.EmployeeLevel;
import lombok.Data;

@Data
public class Employee {
    private String id;
    private String name;
    private EmployeeLevel level;
    private String department;
}
