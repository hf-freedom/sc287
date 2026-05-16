package com.company.car.service;

import com.company.car.dto.ApplicationDTO;
import com.company.car.dto.ReturnCarDTO;
import com.company.car.entity.*;
import com.company.car.enums.ApplicationStatus;
import com.company.car.enums.CarStatus;
import com.company.car.enums.EmployeeLevel;
import com.company.car.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarApplicationService {

    @Autowired
    private InMemoryStorage storage;

    private static final double FUEL_PRICE = 8.5;

    public Map<String, Object> createApplication(ApplicationDTO dto) {
        Employee employee = storage.employees.get(dto.getEmployeeId());
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        List<Car> availableCars = findAvailableCars(dto.getStartTime(), dto.getEndTime(), dto.getPassengers());
        List<CarApplication> conflictingApps = findConflictingApplications(dto.getStartTime(), dto.getEndTime());
        
        Map<String, Object> result = new HashMap<>();
        
        if (dto.isEmergency()) {
            List<CarApplication> preemptibleApps = conflictingApps.stream()
                    .filter(app -> !app.isEmergency())
                    .filter(app -> app.getEmployeePriority() <= employee.getLevel().getPriority())
                    .collect(Collectors.toList());
            
            if (availableCars.isEmpty() && preemptibleApps.isEmpty()) {
                throw new RuntimeException("当前时间段没有可用车辆，也无可抢占的预约");
            }
            
            if (!preemptibleApps.isEmpty()) {
                result.put("willPreempt", true);
                result.put("preemptibleApps", preemptibleApps);
                result.put("preemptCount", preemptibleApps.size());
            } else {
                result.put("willPreempt", false);
            }
        } else {
            if (availableCars.isEmpty()) {
                if (!conflictingApps.isEmpty()) {
                    result.put("conflict", true);
                    result.put("conflictApps", conflictingApps);
                    result.put("suggestion", "建议升级为紧急用车以抢占预约，或选择其他时间段");
                }
                throw new RuntimeException("当前时间段没有可用车辆");
            }
            result.put("conflict", false);
        }

        List<Driver> availableDrivers = findAvailableDrivers(dto.getStartTime(), dto.getEndTime());
        if (availableDrivers.isEmpty()) {
            throw new RuntimeException("当前时间段没有可用司机");
        }

        CarApplication application = new CarApplication();
        application.setId(UUID.randomUUID().toString());
        application.setEmployeeId(employee.getId());
        application.setEmployeeName(employee.getName());
        application.setEmployeePriority(employee.getLevel().getPriority());
        application.setPurpose(dto.getPurpose());
        application.setDestination(dto.getDestination());
        application.setPassengers(dto.getPassengers());
        application.setStartTime(dto.getStartTime());
        application.setEndTime(dto.getEndTime());
        application.setEmergency(dto.isEmergency());
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreateTime(LocalDateTime.now());

        storage.applications.put(application.getId(), application);
        
        result.put("application", application);
        return result;
    }

    private List<CarApplication> findConflictingApplications(LocalDateTime startTime, LocalDateTime endTime) {
        return storage.applications.values().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPROVED)
                .filter(app -> isTimeOverlap(app.getStartTime(), app.getEndTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public Map<String, Object> approveApplication(String applicationId, String remark, boolean approved) {
        CarApplication application = storage.applications.get(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("只能审批待审批的申请");
        }

        Map<String, Object> result = new HashMap<>();

        if (approved) {
            if (application.isEmergency()) {
                List<CarApplication> preemptedApps = handleEmergencyPreemption(application);
                if (!preemptedApps.isEmpty()) {
                    result.put("preempted", true);
                    result.put("preemptedApps", preemptedApps);
                    result.put("preemptedCount", preemptedApps.size());
                } else {
                    result.put("preempted", false);
                }
            } else {
                result.put("preempted", false);
            }

            Car assignedCar = findAndAssignCar(application);
            Driver assignedDriver = findAndAssignDriver(application);

            application.setCarId(assignedCar.getId());
            application.setCarPlateNumber(assignedCar.getPlateNumber());
            application.setDriverId(assignedDriver.getId());
            application.setDriverName(assignedDriver.getName());
            application.setStatus(ApplicationStatus.APPROVED);
            application.setApproveTime(LocalDateTime.now());
            application.setApproveRemark(remark);
        } else {
            application.setStatus(ApplicationStatus.REJECTED);
            application.setApproveTime(LocalDateTime.now());
            application.setApproveRemark(remark);
        }

        result.put("application", application);
        return result;
    }

    private List<CarApplication> handleEmergencyPreemption(CarApplication emergencyApp) {
        List<CarApplication> conflictingApps = storage.applications.values().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPROVED)
                .filter(app -> !app.isEmergency())
                .filter(app -> app.getEmployeePriority() <= emergencyApp.getEmployeePriority())
                .filter(app -> isTimeOverlap(app.getStartTime(), app.getEndTime(),
                        emergencyApp.getStartTime(), emergencyApp.getEndTime()))
                .collect(Collectors.toList());

        for (CarApplication app : conflictingApps) {
            app.setStatus(ApplicationStatus.PENDING);
            app.setCarId(null);
            app.setCarPlateNumber(null);
            app.setDriverId(null);
            app.setDriverName(null);
        }

        return conflictingApps;
    }

    private Car findAndAssignCar(CarApplication application) {
        List<Car> availableCars = findAvailableCars(
                application.getStartTime(),
                application.getEndTime(),
                application.getPassengers()
        );

        if (availableCars.isEmpty()) {
            throw new RuntimeException("没有可用车辆");
        }

        return availableCars.get(0);
    }

    private Driver findAndAssignDriver(CarApplication application) {
        List<Driver> availableDrivers = findAvailableDrivers(
                application.getStartTime(),
                application.getEndTime()
        );

        if (availableDrivers.isEmpty()) {
            throw new RuntimeException("没有可用司机");
        }

        return availableDrivers.get(0);
    }

    public List<Car> findAvailableCars(LocalDateTime startTime, LocalDateTime endTime, int passengers) {
        return storage.cars.values().stream()
                .filter(car -> car.getStatus() == CarStatus.AVAILABLE)
                .filter(car -> car.getSeats() >= passengers)
                .filter(car -> storage.getApplicationsByCarAndTime(car.getId(), startTime, endTime).isEmpty())
                .collect(Collectors.toList());
    }

    public List<Driver> findAvailableDrivers(LocalDateTime startTime, LocalDateTime endTime) {
        return storage.drivers.values().stream()
                .filter(Driver::isAvailable)
                .filter(driver -> storage.getApplicationsByDriverAndTime(driver.getId(), startTime, endTime).isEmpty())
                .collect(Collectors.toList());
    }

    public CarApplication modifyApplication(String applicationId, ApplicationDTO dto) {
        CarApplication application = storage.applications.get(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        if (application.getStatus() == ApplicationStatus.IN_PROGRESS ||
                application.getStatus() == ApplicationStatus.COMPLETED) {
            throw new RuntimeException("当前状态不能修改申请");
        }

        List<Car> availableCars = findAvailableCars(dto.getStartTime(), dto.getEndTime(), dto.getPassengers());
        if (availableCars.isEmpty()) {
            throw new RuntimeException("修改后时间段没有可用车辆");
        }

        List<Driver> availableDrivers = findAvailableDrivers(dto.getStartTime(), dto.getEndTime());
        if (availableDrivers.isEmpty()) {
            throw new RuntimeException("修改后时间段没有可用司机");
        }

        application.setPurpose(dto.getPurpose());
        application.setDestination(dto.getDestination());
        application.setPassengers(dto.getPassengers());
        application.setStartTime(dto.getStartTime());
        application.setEndTime(dto.getEndTime());
        application.setEmergency(dto.isEmergency());

        if (application.getStatus() == ApplicationStatus.APPROVED) {
            application.setStatus(ApplicationStatus.PENDING);
            application.setCarId(null);
            application.setCarPlateNumber(null);
            application.setDriverId(null);
            application.setDriverName(null);
        }

        return application;
    }

    public CarApplication startApplication(String applicationId) {
        CarApplication application = storage.applications.get(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new RuntimeException("只能开始已批准的申请");
        }

        application.setStatus(ApplicationStatus.IN_PROGRESS);
        return application;
    }

    public ExpenseRecord returnCar(ReturnCarDTO dto) {
        CarApplication application = storage.applications.get(dto.getApplicationId());
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        if (application.getStatus() != ApplicationStatus.IN_PROGRESS) {
            throw new RuntimeException("只能归还进行中的申请");
        }

        Car car = storage.cars.get(application.getCarId());
        double startMileage = car.getCurrentMileage();
        double distance = dto.getEndMileage() - startMileage;

        if (distance < 0) {
            throw new RuntimeException("结束里程不能小于开始里程");
        }

        ExpenseRecord record = new ExpenseRecord();
        record.setId(UUID.randomUUID().toString());
        record.setApplicationId(application.getId());
        record.setCarId(car.getId());
        record.setCarPlateNumber(car.getPlateNumber());
        record.setStartMileage(startMileage);
        record.setEndMileage(dto.getEndMileage());
        record.setDistance(distance);
        record.setFuelUsed(dto.getFuelUsed());
        record.setFuelCost(dto.getFuelUsed() * FUEL_PRICE);
        record.setTotalCost(dto.getFuelUsed() * FUEL_PRICE);
        record.setCreateTime(LocalDateTime.now());

        storage.expenseRecords.put(record.getId(), record);

        car.setCurrentMileage(dto.getEndMileage());

        application.setStatus(ApplicationStatus.COMPLETED);
        application.setEndMileage(dto.getEndMileage());
        application.setFuelUsed(dto.getFuelUsed());

        return record;
    }

    public List<CarApplication> getAllApplications() {
        return new ArrayList<>(storage.applications.values());
    }

    public List<Car> getAllCars() {
        return new ArrayList<>(storage.cars.values());
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(storage.drivers.values());
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(storage.employees.values());
    }

    public List<ExpenseRecord> getAllExpenseRecords() {
        return new ArrayList<>(storage.expenseRecords.values());
    }

    public Map<String, Object> getScheduledCheckResult() {
        Map<String, Object> result = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soon = now.plusHours(2);

        List<CarApplication> upcomingDepartures = storage.applications.values().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPROVED)
                .filter(app -> app.getStartTime().isAfter(now) && app.getStartTime().isBefore(soon))
                .collect(Collectors.toList());

        List<CarApplication> overdueReturns = storage.applications.values().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.IN_PROGRESS)
                .filter(app -> app.getEndTime().isBefore(now))
                .collect(Collectors.toList());

        List<Car> maintenanceDue = storage.cars.values().stream()
                .filter(car -> car.getLastMaintenanceDate() != null)
                .filter(car -> {
                    LocalDate nextMaintenance = car.getLastMaintenanceDate().plusMonths(6);
                    return nextMaintenance.isBefore(LocalDate.now().plusWeeks(1));
                })
                .collect(Collectors.toList());

        result.put("upcomingDepartures", upcomingDepartures);
        result.put("overdueReturns", overdueReturns);
        result.put("maintenanceDue", maintenanceDue);

        return result;
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
