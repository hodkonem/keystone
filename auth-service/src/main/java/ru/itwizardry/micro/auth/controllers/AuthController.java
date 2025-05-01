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
import ru.itwizardry.micro.common.jwt.entities.Role;
import ru.itwizardry.micro.auth.entities.User;
import ru.itwizardry.micro.auth.services.AuthService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
        private static final String KEY_STATUS = "status";
    private static final String KEY_ERROR = "error";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN = "token";

    private static final String STATUS_REGISTER_SUCCESS = "User registered successfully";
    private static final String STATUS_REGISTER_FAILED = "Registration failed";
    private static final String STATUS_LOGIN_SUCCESS = "Login successful";
    private static final String STATUS_LOGIN_FAILED = "Login failed";

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
                            KEY_ID, user.getId(),
                            KEY_USERNAME, user.getUsername(),
                            KEY_STATUS, STATUS_REGISTER_SUCCESS));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            KEY_ERROR, e.getMessage(),
                            KEY_STATUS, STATUS_REGISTER_FAILED));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        try {
            String token = authService.loginUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(Map.of(
                    KEY_TOKEN, token,
                    KEY_STATUS, STATUS_LOGIN_SUCCESS));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            KEY_ERROR, "Invalid credentials",
                            KEY_STATUS, STATUS_LOGIN_FAILED));
        }
    }
}