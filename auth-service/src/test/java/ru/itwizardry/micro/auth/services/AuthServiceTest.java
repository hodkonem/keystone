package ru.itwizardry.micro.auth.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.itwizardry.micro.auth.entities.Role;
import ru.itwizardry.micro.auth.entities.User;
import ru.itwizardry.micro.auth.repositories.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String jwtSecret = "jwtSecretKey155556789666645678901237777777";

    @BeforeEach
    void setup() {
        authService = new AuthService(userRepository, passwordEncoder, jwtSecret);
    }

    @Test
    void registerUserSuccess() {
        String username = "testuser";
        String password = "password";
        Set<Role> roles = Set.of(Role.ROLE_USER);
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setPassword("encodedPassword");
        expectedUser.setRoles(roles);

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = authService.registerUser(username, password, roles);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(roles, result.getRoles());
        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUserUsernameExistsThrowsException() {
        String username = "existinguser";
        String password = "password";
        Set<Role> roles = Set.of(Role.ROLE_USER);

        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(username, password, roles);
        });
    }

    @Test
    void loginUserSuccess() {
        String username = "testuser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        String token = authService.loginUser(username, password);

        assertNotNull(token);
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }

    @Test
    void loginUserWhenPasswordIncorrectThenThrowsBadCredentialException() {
        String username = "nonexistent";
        String password = "wrongpassword";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authService.loginUser(username, password);
        });
    }

    @Test
    void loginUserIfWrongPasswordThenException() {
        String username = "testuser";
        String password = "wrongpassword";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authService.loginUser(username, password);
        });
    }
}