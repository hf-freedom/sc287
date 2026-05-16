package com.company.car.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScheduledTaskService {

    @Autowired
    private CarApplicationService carApplicationService;

    private Map<String, Object> lastCheckResult;

    @Scheduled(cron = "0 0 * * * ?")
    public void checkVehiclesAndApplications() {
        lastCheckResult = carApplicationService.getScheduledCheckResult();
    }

    public Map<String, Object> getLastCheckResult() {
        if (lastCheckResult == null) {
            lastCheckResult = carApplicationService.getScheduledCheckResult();
        }
        return lastCheckResult;
    }
}
