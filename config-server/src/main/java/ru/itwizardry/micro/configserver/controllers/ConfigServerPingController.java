package ru.itwizardry.micro.configserver.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigServerPingController {

    @GetMapping("/ping")
    public String ping() {
        return "Config Server is operational";
    }
}
