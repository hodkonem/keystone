package ru.itwizardry.micro.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.itwizardry.micro.auth.model.roles.ApplicationRole;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class JwtUtils {

    public static final int MIN_JWT_LENGTH = 32;

    private JwtUtils() {
    }

    public static Claims validateAndExtractClaims(String jwt, SecretKey key) throws JwtException {
        Objects.requireNonNull(jwt, "JWT cannot be null");

        if (jwt.length() < MIN_JWT_LENGTH) {
            throw new MalformedJwtException("Token too short");
        }

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public static List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        if (roles == null || roles.isEmpty()) {
            throw new JwtException("No roles in token");
        }
        return roles.stream()
                .map(ApplicationRole::fromSting)
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    public static String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public static SecretKey getSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}