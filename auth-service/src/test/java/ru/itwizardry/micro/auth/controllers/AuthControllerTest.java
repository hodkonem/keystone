package ru.itwizardry.micro.auth.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import ru.itwizardry.micro.auth.dto.LoginRequest;
import ru.itwizardry.micro.auth.dto.RegisterRequest;
import ru.itwizardry.micro.auth.entities.Role;
import ru.itwizardry.micro.auth.entities.User;
import ru.itwizardry.micro.auth.services.AuthService;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRoles(Set.of(Role.ROLE_USER));

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
    }

    @Test
    void register_Success() {
        when(authService.registerUser(anyString(), anyString(), anySet())).thenReturn(testUser);

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(1L, body.get("id"));
        assertEquals("testuser", body.get("username"));
    }

    @Test
    void register_UsernameExists_ReturnsBadRequest() {
        when(authService.registerUser(anyString(), anyString(), anySet()))
                .thenThrow(new IllegalArgumentException("username already exists"));

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertTrue(body.containsKey("error"));
        assertEquals("username already exists", body.get("error"));
    }

    @Test
    void login_Success() {
        when(authService.loginUser(anyString(), anyString())).thenReturn("testToken");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("testToken", body.get("token"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() {
        when(authService.loginUser(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Map.class, response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Invalid credentials", body.get("error"));
    }

}