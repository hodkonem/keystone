package ru.itwizardry.micro.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtKeyFactory;
import ru.itwizardry.micro.common.jwt.JwtService;

import java.time.Duration;

@Configuration
public class JwtServiceConfig {

    @Bean
    public JwtService jwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-hours:24}") long expirationHours) {
        return new DefaultJwtService(
                JwtKeyFactory.fromSecret(secret),
                Duration.ofHours(expirationHours)
        );
    }
}

