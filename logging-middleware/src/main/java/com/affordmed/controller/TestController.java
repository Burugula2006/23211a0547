package com.affordmed.controller;

import com.affordmed.service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private LoggerService loggerService;

    @GetMapping("/test")
    public String test() {

        loggerService.log(
                "backend",
                "info",
                "controller",
                "Test endpoint called"
        );

        return "Logging Middleware Working";

    }

}