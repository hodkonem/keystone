package ru.itwizardry.micro.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdminController {

    @GetMapping("/admin")
    public ResponseEntity<String> getAdminData() {
        return ResponseEntity.ok("Привет, админ!");
    }
}

