package ru.itwizardry.micro.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultJwtService implements JwtService {
    public static final int MIN_JWT_LENGTH = 32;
    private final SecretKey key;
    private final long expirationMs;

    public DefaultJwtService(String secret, long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    @Override
    public Claims validateAndExtractClaims(String jwt) throws JwtException {
        if (jwt.length() < MIN_JWT_LENGTH) {
            throw new MalformedJwtException("Token too short");
        }
        if (jwt.chars().filter(c -> c == '.').count() != 2) {
            throw new MalformedJwtException("Invalid JWT format");
        }

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    @Override
    public List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<String> roles = claims.get("authorities", List.class);

        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public Long extractUserId(Claims claims) {
        return claims.get("userId", Long.class);
    }

    public String generateToken(UserDetails userDetails, Long userId) {
        validateUserDetails(userDetails);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userId)
                .claim("authorities", getRoles(userDetails))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = validateAndExtractClaims(token);
            String username = extractUsername(claims);
            return username.equals(userDetails.getUsername());
        } catch (JwtException e) {
            return false;
        }
    }

    private List<String> getRoles(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    private void validateUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails cannot be null");
        }
        if (userDetails.getUsername() == null || userDetails.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (userDetails.getAuthorities() == null) {
            throw new IllegalArgumentException("Authorities cannot be null");
        }
    }
}