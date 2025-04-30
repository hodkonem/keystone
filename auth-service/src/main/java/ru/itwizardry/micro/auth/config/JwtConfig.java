package ru.itwizardry.micro.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtService;

@Configuration
public class JwtConfig {

    @Bean
    public JwtService jwtService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        return new DefaultJwtService(jwtSecret, expirationMs);
    }
}