package ru.itwizardry.micro.order.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtService;
import ru.itwizardry.micro.order.dto.OrderRequest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(OrderControllerIntegrationTest.TestConfig.class)
@Testcontainers
class OrderControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtService jwtService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtService jwtService() {
            String secret = "testtesttesttesttesttesttesttest"; // 32+ символов
            long expirationMs = 3600000;
            return new DefaultJwtService(secret, expirationMs);
        }
    }

    @Test
    void createOrder_ShouldReturn201_WhenValidToken() {
        var userDetails = new User("123", "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String jwt = jwtService.generateToken(userDetails, 123L);

        OrderRequest orderRequest = new OrderRequest(1L, 2);

        webTestClient.post()
                .uri("/orders")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void createOrder_ShouldReturn401_WhenNoToken() {
        webTestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new OrderRequest(1L, 2))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}