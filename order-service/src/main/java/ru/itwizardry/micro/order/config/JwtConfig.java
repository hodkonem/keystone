package ru.itwizardry.micro.order.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.common.jwt.DefaultJwtService;
import ru.itwizardry.micro.common.jwt.JwtProperties;
import ru.itwizardry.micro.common.jwt.JwtService;
import ru.itwizardry.micro.common.jwt.JwtKeyFactory;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public JwtService jwtService(JwtProperties props) {
        return new DefaultJwtService(
                JwtKeyFactory.fromSecret(props.getSecret()),
                props.getExpiration()
        );
    }
}