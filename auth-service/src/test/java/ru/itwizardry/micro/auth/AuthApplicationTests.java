package ru.itwizardry.micro.auth;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthApplicationTests {

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        flyway.clean(); // Очищает базу данных
        flyway.migrate(); // Выполняет миграции
    }

    @Test
    void contextLoads() {
    }
}