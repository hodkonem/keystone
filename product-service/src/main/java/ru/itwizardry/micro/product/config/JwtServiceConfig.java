package ru.itwizardry.micro.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtKeyFactory;
import ru.itwizardry.micro.common.jwt.JwtProperties;
import ru.itwizardry.micro.common.jwt.JwtService;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtServiceConfig {

    @Bean
    public JwtService jwtService(JwtProperties jwtProperties) {
        return new DefaultJwtService(
                JwtKeyFactory.fromSecret(jwtProperties.getSecret()),
                jwtProperties.getExpiration()
        );
    }
}