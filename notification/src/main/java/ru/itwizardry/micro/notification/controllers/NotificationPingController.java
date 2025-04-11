package ru.itwizardry.micro.notification.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationPingController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/ping")
    public String ping() {
        return "OK from " + appName;
    }
}
