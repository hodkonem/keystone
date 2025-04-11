package ru.itwizardry.micro.gateway.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GatewayPingController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "service", "gateway",
                "routes", "[/api/auth, api/products]",
                "status", "UP"
        );
    }
}
