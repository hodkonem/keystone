package ru.itwizardry.micro.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.itwizardry.micro.testcontainers.BaseIntegrationTest;

@SpringBootTest
@ActiveProfiles("test")
class AuthApplicationTests extends BaseIntegrationTest {

    /**
     * Тест проверяет успешную загрузку Spring контекста приложения.
     * Пустое тело метода - нормальная практика для таких проверок.
     */
    @Test
    void contextLoads() {
        // Intentional no-op: тестируется только загрузка контекста
    }
}