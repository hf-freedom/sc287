package com.company.car.storage;

import com.company.car.entity.*;
import com.company.car.enums.ApplicationStatus;
import com.company.car.enums.CarStatus;
import com.company.car.enums.EmployeeLevel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {

    public final Map<String, Employee> employees = new ConcurrentHashMap<>();
    public final Map<String, Car> cars = new ConcurrentHashMap<>();
    public final Map<String, Driver> drivers = new ConcurrentHashMap<>();
    public final Map<String, CarApplication> applications = new ConcurrentHashMap<>();
    public final Map<String, ExpenseRecord> expenseRecords = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        initEmployees();
        initCars();
        initDrivers();
        initApplications();
        initExpenseRecords();
    }

    private void initExpenseRecords() {
        ExpenseRecord record1 = new ExpenseRecord();
        record1.setId("R001");
        record1.setApplicationId("A004");
        record1.setCarId("C003");
        record1.setCarPlateNumber("京C11111");
        record1.setStartMileage(80000.0);
        record1.setEndMileage(80150.0);
        record1.setDistance(150.0);
        record1.setFuelUsed(18.0);
        record1.setFuelCost(153.0);
        record1.setTotalCost(153.0);
        record1.setCreateTime(LocalDateTime.now().minusDays(1));
        expenseRecords.put(record1.getId(), record1);
    }

    private void initApplications() {
        CarApplication app1 = new CarApplication();
        app1.setId("A001");
        app1.setEmployeeId("E001");
        app1.setEmployeeName("张三");
        app1.setEmployeePriority(1);
        app1.setPurpose("项目会议");
        app1.setDestination("上海分公司");
        app1.setPassengers(3);
        app1.setStartTime(LocalDateTime.now().plusHours(2));
        app1.setEndTime(LocalDateTime.now().plusHours(6));
        app1.setStatus(ApplicationStatus.PENDING);
        app1.setEmergency(false);
        app1.setCreateTime(LocalDateTime.now());
        applications.put(app1.getId(), app1);

        CarApplication app2 = new CarApplication();
        app2.setId("A002");
        app2.setEmployeeId("E002");
        app2.setEmployeeName("李四");
        app2.setEmployeePriority(2);
        app2.setPurpose("客户拜访");
        app2.setDestination("杭州客户公司");
        app2.setPassengers(2);
        app2.setStartTime(LocalDateTime.now().plusDays(1));
        app2.setEndTime(LocalDateTime.now().plusDays(1).plusHours(8));
        app2.setCarId("C001");
        app2.setCarPlateNumber("京A12345");
        app2.setDriverId("D001");
        app2.setDriverName("刘师傅");
        app2.setStatus(ApplicationStatus.APPROVED);
        app2.setEmergency(false);
        app2.setCreateTime(LocalDateTime.now());
        app2.setApproveTime(LocalDateTime.now());
        applications.put(app2.getId(), app2);

        CarApplication app3 = new CarApplication();
        app3.setId("A003");
        app3.setEmployeeId("E003");
        app3.setEmployeeName("王五");
        app3.setEmployeePriority(3);
        app3.setPurpose("机场接送");
        app3.setDestination("浦东机场");
        app3.setPassengers(1);
        app3.setStartTime(LocalDateTime.now().minusHours(1));
        app3.setEndTime(LocalDateTime.now().plusHours(1));
        app3.setCarId("C002");
        app3.setCarPlateNumber("京B67890");
        app3.setDriverId("D002");
        app3.setDriverName("陈师傅");
        app3.setStatus(ApplicationStatus.IN_PROGRESS);
        app3.setEmergency(true);
        app3.setCreateTime(LocalDateTime.now().minusHours(2));
        app3.setApproveTime(LocalDateTime.now().minusHours(2));
        applications.put(app3.getId(), app3);

        CarApplication app4 = new CarApplication();
        app4.setId("A004");
        app4.setEmployeeId("E004");
        app4.setEmployeeName("赵六");
        app4.setEmployeePriority(4);
        app4.setPurpose("商务接待");
        app4.setDestination("五星级酒店");
        app4.setPassengers(4);
        app4.setStartTime(LocalDateTime.now().minusDays(2));
        app4.setEndTime(LocalDateTime.now().minusDays(2).plusHours(5));
        app4.setCarId("C003");
        app4.setCarPlateNumber("京C11111");
        app4.setDriverId("D003");
        app4.setDriverName("周师傅");
        app4.setStatus(ApplicationStatus.COMPLETED);
        app4.setEmergency(false);
        app4.setCreateTime(LocalDateTime.now().minusDays(3));
        app4.setApproveTime(LocalDateTime.now().minusDays(3));
        app4.setEndMileage(80150.0);
        app4.setFuelUsed(45.0);
        applications.put(app4.getId(), app4);
    }

    private void initEmployees() {
        Employee emp1 = new Employee();
        emp1.setId("E001");
        emp1.setName("张三");
        emp1.setLevel(EmployeeLevel.JUNIOR);
        emp1.setDepartment("技术部");
        employees.put(emp1.getId(), emp1);

        Employee emp2 = new Employee();
        emp2.setId("E002");
        emp2.setName("李四");
        emp2.setLevel(EmployeeLevel.MIDDLE);
        emp2.setDepartment("市场部");
        employees.put(emp2.getId(), emp2);

        Employee emp3 = new Employee();
        emp3.setId("E003");
        emp3.setName("王五");
        emp3.setLevel(EmployeeLevel.SENIOR);
        emp3.setDepartment("总经理室");
        employees.put(emp3.getId(), emp3);

        Employee emp4 = new Employee();
        emp4.setId("E004");
        emp4.setName("赵六");
        emp4.setLevel(EmployeeLevel.EXECUTIVE);
        emp4.setDepartment("董事会");
        employees.put(emp4.getId(), emp4);
    }

    private void initCars() {
        Car car1 = new Car();
        car1.setId("C001");
        car1.setPlateNumber("京A12345");
        car1.setBrand("奥迪");
        car1.setModel("A6");
        car1.setSeats(5);
        car1.setStatus(CarStatus.AVAILABLE);
        car1.setCurrentMileage(50000);
        car1.setFuelConsumption(8.5);
        car1.setLastMaintenanceDate(LocalDate.now().minusMonths(1));
        car1.setMaintenanceIntervalKm(10000);
        cars.put(car1.getId(), car1);

        Car car2 = new Car();
        car2.setId("C002");
        car2.setPlateNumber("京B67890");
        car2.setBrand("奔驰");
        car2.setModel("E300");
        car2.setSeats(5);
        car2.setStatus(CarStatus.AVAILABLE);
        car2.setCurrentMileage(30000);
        car2.setFuelConsumption(9.0);
        car2.setLastMaintenanceDate(LocalDate.now().minusMonths(2));
        car2.setMaintenanceIntervalKm(10000);
        cars.put(car2.getId(), car2);

        Car car3 = new Car();
        car3.setId("C003");
        car3.setPlateNumber("京C11111");
        car3.setBrand("别克");
        car3.setModel("GL8");
        car3.setSeats(7);
        car3.setStatus(CarStatus.AVAILABLE);
        car3.setCurrentMileage(80000);
        car3.setFuelConsumption(11.0);
        car3.setLastMaintenanceDate(LocalDate.now().minusMonths(3));
        car3.setMaintenanceIntervalKm(10000);
        cars.put(car3.getId(), car3);

        Car car4 = new Car();
        car4.setId("C004");
        car4.setPlateNumber("京D22222");
        car4.setBrand("丰田");
        car4.setModel("凯美瑞");
        car4.setSeats(5);
        car4.setStatus(CarStatus.MAINTENANCE);
        car4.setCurrentMileage(60000);
        car4.setFuelConsumption(7.5);
        car4.setLastMaintenanceDate(LocalDate.now().minusMonths(6));
        car4.setMaintenanceIntervalKm(10000);
        cars.put(car4.getId(), car4);
    }

    private void initDrivers() {
        Driver driver1 = new Driver();
        driver1.setId("D001");
        driver1.setName("刘师傅");
        driver1.setPhone("13800138001");
        driver1.setLicenseNumber("A12345678");
        driver1.setAvailable(true);
        drivers.put(driver1.getId(), driver1);

        Driver driver2 = new Driver();
        driver2.setId("D002");
        driver2.setName("陈师傅");
        driver2.setPhone("13800138002");
        driver2.setLicenseNumber("A87654321");
        driver2.setAvailable(true);
        drivers.put(driver2.getId(), driver2);

        Driver driver3 = new Driver();
        driver3.setId("D003");
        driver3.setName("周师傅");
        driver3.setPhone("13800138003");
        driver3.setLicenseNumber("A11223344");
        driver3.setAvailable(true);
        drivers.put(driver3.getId(), driver3);
    }

    public List<CarApplication> getApplicationsByCarAndTime(String carId, LocalDateTime startTime, LocalDateTime endTime) {
        List<CarApplication> result = new ArrayList<>();
        for (CarApplication app : applications.values()) {
            if (app.getCarId() != null && app.getCarId().equals(carId)) {
                if (app.getStatus() == ApplicationStatus.APPROVED || app.getStatus() == ApplicationStatus.IN_PROGRESS) {
                    if (isTimeOverlap(app.getStartTime(), app.getEndTime(), startTime, endTime)) {
                        result.add(app);
                    }
                }
            }
        }
        return result;
    }

    public List<CarApplication> getApplicationsByDriverAndTime(String driverId, LocalDateTime startTime, LocalDateTime endTime) {
        List<CarApplication> result = new ArrayList<>();
        for (CarApplication app : applications.values()) {
            if (app.getDriverId() != null && app.getDriverId().equals(driverId)) {
                if (app.getStatus() == ApplicationStatus.APPROVED || app.getStatus() == ApplicationStatus.IN_PROGRESS) {
                    if (isTimeOverlap(app.getStartTime(), app.getEndTime(), startTime, endTime)) {
                        result.add(app);
                    }
                }
            }
        }
        return result;
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
