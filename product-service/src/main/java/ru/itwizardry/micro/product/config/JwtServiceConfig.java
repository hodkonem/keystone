package ru.itwizardry.micro.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtService;

@Configuration
public class JwtServiceConfig {

    @Bean
    public JwtService jwtService(@Value("${jwt.secret}") String jwtSecret) {
        return new DefaultJwtService(jwtSecret);
    }
}
