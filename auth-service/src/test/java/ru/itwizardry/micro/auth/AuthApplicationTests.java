package ru.itwizardry.micro.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.itwizardry.micro.auth.config.TestJwtConfig;
import ru.itwizardry.micro.auth.config.TestMockConfig;
import ru.itwizardry.micro.testcontainers.BaseIntegrationTest;

@Import(TestMockConfig.class)
@SpringBootTest(classes = {AuthApplication.class, TestJwtConfig.class})
@ActiveProfiles("test")
class AuthApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }
}