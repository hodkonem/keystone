package ru.itwizardry.micro.auth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtService;

@TestConfiguration
public class TestJwtConfig {

    @Bean
    @Primary
    public JwtService jwtService() {
        return new DefaultJwtService(
                "testSecretKey155556789666645678901237777777",
                86400000
        );
    }
}