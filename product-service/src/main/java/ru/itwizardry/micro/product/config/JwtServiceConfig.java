package ru.itwizardry.micro.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtService;

import java.time.Duration;

@Configuration
public class JwtServiceConfig {

    @Bean
    @ConfigurationProperties(prefix = "jwt")
    public JwtProperties jwtProperties() {
        return new JwtProperties("", Duration.ofHours(24));
    }

    @Bean
    public JwtService jwtService(JwtProperties jwtProperties) {
        return new DefaultJwtService(
                jwtProperties.secret(),
                jwtProperties.expiration().toMillis()
        );
    }

    public record JwtProperties(
            String secret,
            Duration expiration
    ) {
        public JwtProperties {
            if (secret == null || secret.length() < DefaultJwtService.MIN_JWT_LENGTH) {
                throw new IllegalArgumentException(
                        "JWT secret must be at least " + DefaultJwtService.MIN_JWT_LENGTH + " characters long");
            }
        }
    }
}