package ru.itwizardry.micro.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface JwtService {
    Claims validateAndExtractClaims(String jwt) throws JwtException;

    List<GrantedAuthority> extractAuthorities(Claims claims);

    String extractUsername(Claims claims);

    Long extractUserId(Claims claims);

    String generateToken(UserDetails userDetails, Long userId);
}
