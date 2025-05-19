package ru.itwizardry.micro.auth.controllers;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itwizardry.micro.common.jwt.JwtService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:15:///test_db",
        "spring.datasource.username=user",
        "spring.datasource.password=pass",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private final String adminToken = "mock-token-admin";
    private final String userToken = "mock-token-user";

    private UserDetails admin;
    private UserDetails user;

    @BeforeEach
    void setUpMocks() {
        admin = User.withUsername("admin")
                .password("pass")
                .roles("ADMIN")
                .build();

        user = User.withUsername("user")
                .password("pass")
                .roles("USER")
                .build();

        Claims adminClaims = mock(Claims.class);
        when(jwtService.validateAndExtractClaims(adminToken)).thenReturn(adminClaims);
        when(jwtService.extractUsername(adminClaims)).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(admin);
        when(jwtService.isTokenValid(adminToken, admin)).thenReturn(true);

        Claims userClaims = mock(Claims.class);
        when(jwtService.validateAndExtractClaims(userToken)).thenReturn(userClaims);
        when(jwtService.extractUsername(userClaims)).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        when(jwtService.isTokenValid(userToken, user)).thenReturn(true);
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void accessAdminEndpoint_WithAdminRole_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void accessAdminEndpoint_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessAdminEndpoint_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized());
    }
}