package ru.itwizardry.micro.auth.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.itwizardry.micro.auth.repositories.UserRepository;
import ru.itwizardry.micro.auth.services.AuthService;
import ru.itwizardry.micro.common.jwt.JwtService;

@TestConfiguration
public class TestMockConfig {

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }
}
