package ru.itwizardry.micro.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.itwizardry.micro.common.jwt.JwtService;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtConfigTest {

    @Test
    void shouldCreateJwtService_WithValidProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("testSecretKey12345678901234567890123456789012");
        properties.setExpiration(3600000L);

        JwtConfig config = new JwtConfig();

        JwtService jwtService = config.jwtService(properties);

        UserDetails user = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(() -> "ROLE_USER");
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return "user";
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };

        String token = jwtService.generateToken(user, 1L);

        assertThat(token).isNotBlank();
    }
}