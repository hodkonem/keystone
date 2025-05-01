package ru.itwizardry.micro.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// CHECKSTYLE IGNORE ImportDeclarationCheck FOR NEXT 1 LINES
import ru.itwizardry.micro.common.jwt.entities.Role;
@RestController
@RequestMapping("/api")
public class AdminController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> getAdminData() {
        return ResponseEntity.ok("Привет, админ!");
    }
}

