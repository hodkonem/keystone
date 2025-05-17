package ru.itwizardry.micro.auth.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itwizardry.micro.auth.repositories.UserRepository;
import ru.itwizardry.micro.auth.services.AuthService;
import ru.itwizardry.micro.common.jwt.JwtService;

@Configuration
public class TestMockConfig {

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }
}
