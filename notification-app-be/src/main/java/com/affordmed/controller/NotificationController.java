package com.affordmed.controller;

import com.affordmed.dto.Notification;
import com.affordmed.service.LoggerService;
import com.affordmed.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LoggerService loggerService;

    @GetMapping("/priority")
    public ResponseEntity<List<Notification>> getTopN(
            @RequestParam(defaultValue = "10") int n) {

        loggerService.log("backend", "info", "controller",
                "Priority inbox requested for top " + n);

        List<Notification> result = notificationService.getTopN(n);

        return ResponseEntity.ok(result);
    }

    // Get all notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {

        loggerService.log("backend", "info", "controller",
                "All notifications requested");

        return ResponseEntity.ok(
                notificationService.fetchNotifications());
    }
}