package ru.itwizardry.micro.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.auth.util.JwtService;
import ru.itwizardry.micro.auth.util.impl.DefaultJwtService;

@Configuration
public class JwtConfig {

    @Bean
    public JwtService jwtService(@Value("${jwt.secret}") String jwtSecret) {
        return new DefaultJwtService(jwtSecret);
    }
}