package ru.itwizardry.micro.common.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public JwtService jwtService(JwtProperties props) {
        return new DefaultJwtService(
                JwtKeyFactory.fromSecret(props.getSecret()),
                props.getExpiration());
    }
}