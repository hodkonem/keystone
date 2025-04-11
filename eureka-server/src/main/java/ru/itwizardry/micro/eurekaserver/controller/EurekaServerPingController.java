package ru.itwizardry.micro.eurekaserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EurekaServerPingController {

    @GetMapping("/ping")
    public String ping() {
        return "Eureka Registry is ready";
    }
}
