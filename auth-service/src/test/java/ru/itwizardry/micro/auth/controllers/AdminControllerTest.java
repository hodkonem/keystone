package ru.itwizardry.micro.auth.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.util.Collections;

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

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void accessAdminEndpoint_WithAdminRole_ShouldReturn200() throws Exception {
        String token = "mock-token";
        String username = "admin";

        UserDetails adminUser = User.builder()
                .username(username)
                .password("password")
                .authorities(Collections.singleton(() -> "ROLE_ADMIN"))
                .build();

        Mockito.when(jwtService.extractUsername(token)).thenReturn(username);
        Mockito.when(jwtService.isTokenValid(token, adminUser)).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(adminUser);

        mockMvc.perform(get("/api/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accessAdminEndpoint_WithUserRole_ShouldReturn403() throws Exception {
        String token = "mock-token";
        String username = "user";

        UserDetails user = User.builder()
                .username(username)
                .password("password")
                .authorities(Collections.singleton(() -> "ROLE_USER"))
                .build();

        Mockito.when(jwtService.extractUsername(token)).thenReturn(username);
        Mockito.when(jwtService.isTokenValid(token, user)).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/api/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessAdminEndpoint_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized());
    }
}