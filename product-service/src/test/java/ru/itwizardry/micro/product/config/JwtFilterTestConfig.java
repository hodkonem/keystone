package ru.itwizardry.micro.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.itwizardry.micro.common.jwt.JwtService;
import ru.itwizardry.micro.common.jwt.filters.JwtAuthenticationFilter;

@Configuration
public class JwtFilterTestConfig {
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                                                           UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}