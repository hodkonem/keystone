package ru.itwizardry.micro.auth.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itwizardry.micro.common.jwt.JwtService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({AuthSecurityConfiguration.class, TestJwtConfig.class})
class JwtAuthenticationFilterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                String.format("jdbc:postgresql://localhost:%d/testdb", postgres.getFirstMappedPort()));
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @BeforeAll
    static void initDatabase() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        String.format("jdbc:postgresql://localhost:%d/testdb", postgres.getFirstMappedPort()),
                        postgres.getUsername(),
                        postgres.getPassword()
                )
                .cleanDisabled(false)
                .load();
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldNotFilter_PermitAllEndpoints() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    void accessAdminEndpoint_WithUserRole_ShouldReturn403() throws Exception {
        User user = new User("user", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_WithValidJwt_ShouldSucceed() throws Exception {
        User user = new User("user", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtService.generateToken(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/protected")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedEndpoint_WithInvalidJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/protected")
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void accessProtectedEndpoint_WithMockUser_ShouldSucceed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/protected"))
                .andExpect(status().isOk());
    }

    private static class User extends org.springframework.security.core.userdetails.User {
        public User(String username, String password, List<GrantedAuthority> authorities) {
            super(username, password, authorities);
        }
    }
}