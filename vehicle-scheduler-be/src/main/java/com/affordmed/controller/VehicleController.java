package com.affordmed.controller;

import com.affordmed.dto.ScheduleResponse;
import com.affordmed.service.LoggerService;
import com.affordmed.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vehicle-scheduling")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LoggerService loggerService;

    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleResponse>> schedule() {

        loggerService.log("backend", "info", "controller",
                "Schedule endpoint hit");

        List<ScheduleResponse> result = vehicleService.schedule();

        loggerService.log("backend", "info", "controller",
                "Schedule complete, depots processed: " + result.size());

        return ResponseEntity.ok(result);
    }
}