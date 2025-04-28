package ru.itwizardry.micro.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface JwtService {
    Claims validateAndExtractClaims(String jwt) throws JwtException;

    List<GrantedAuthority> extractAuthorities(Claims claims);

    String extractUsername(Claims claims);
}
