package ru.itwizardry.micro.auth.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itwizardry.micro.auth.dto.LoginRequest;
import ru.itwizardry.micro.auth.dto.RegisterRequest;
import ru.itwizardry.micro.auth.entities.Role;
import ru.itwizardry.micro.auth.entities.User;
import ru.itwizardry.micro.auth.services.AuthService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterRequest request) {
        try {
            User user = authService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    Set.of(Role.ROLE_USER));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "status", "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", e.getMessage(),
                            "status", "Registration failed"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        try {
            String token = authService.loginUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "status", "Login successful"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Invalid username or password",
                            "status", "Login failed"));
        }
    }
}