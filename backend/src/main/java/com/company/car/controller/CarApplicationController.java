package com.company.car.controller;

import com.company.car.dto.ApplicationDTO;
import com.company.car.dto.Result;
import com.company.car.dto.ReturnCarDTO;
import com.company.car.entity.*;
import com.company.car.service.CarApplicationService;
import com.company.car.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CarApplicationController {

    @Autowired
    private CarApplicationService carApplicationService;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @PostMapping("/applications")
    public Result<Map<String, Object>> createApplication(@RequestBody ApplicationDTO dto) {
        try {
            Map<String, Object> result = carApplicationService.createApplication(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/applications/{id}/approve")
    public Result<Map<String, Object>> approveApplication(
            @PathVariable String id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remark) {
        try {
            Map<String, Object> result = carApplicationService.approveApplication(id, remark, approved);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/applications/{id}")
    public Result<CarApplication> modifyApplication(
            @PathVariable String id,
            @RequestBody ApplicationDTO dto) {
        try {
            CarApplication application = carApplicationService.modifyApplication(id, dto);
            return Result.success(application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/applications/{id}/start")
    public Result<CarApplication> startApplication(@PathVariable String id) {
        try {
            CarApplication application = carApplicationService.startApplication(id);
            return Result.success(application);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/return-car")
    public Result<ExpenseRecord> returnCar(@RequestBody ReturnCarDTO dto) {
        try {
            ExpenseRecord record = carApplicationService.returnCar(dto);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/applications")
    public Result<List<CarApplication>> getAllApplications() {
        return Result.success(carApplicationService.getAllApplications());
    }

    @GetMapping("/cars")
    public Result<List<Car>> getAllCars() {
        return Result.success(carApplicationService.getAllCars());
    }

    @GetMapping("/drivers")
    public Result<List<Driver>> getAllDrivers() {
        return Result.success(carApplicationService.getAllDrivers());
    }

    @GetMapping("/employees")
    public Result<List<Employee>> getAllEmployees() {
        return Result.success(carApplicationService.getAllEmployees());
    }

    @GetMapping("/expenses")
    public Result<List<ExpenseRecord>> getAllExpenses() {
        return Result.success(carApplicationService.getAllExpenseRecords());
    }

    @GetMapping("/scheduled-check")
    public Result<Map<String, Object>> getScheduledCheck() {
        return Result.success(scheduledTaskService.getLastCheckResult());
    }
}
