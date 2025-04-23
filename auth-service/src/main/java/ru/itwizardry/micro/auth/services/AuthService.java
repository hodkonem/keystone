package ru.itwizardry.micro.auth.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itwizardry.micro.auth.entities.Role;
import ru.itwizardry.micro.auth.entities.User;
import ru.itwizardry.micro.auth.repositories.UserRepository;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final String jwtSecret;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, @Value("${jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtSecret = jwtSecret;
    }

    public User registerUser(String username, String password, Set<Role> roles) {
        if (password == null || !username.matches("^[a-zA-Z0-9._-]{3,}$")) {
            throw new IllegalArgumentException("Invalid username format");
        }

        if (roles == null || roles.isEmpty()) {
            roles = Set.of(Role.ROLE_USER);
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", roleNames)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(24 * 60 * 60)))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isUrlAccessible(String url) {
        return url.equals("/register") || url.equals("/login");
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}