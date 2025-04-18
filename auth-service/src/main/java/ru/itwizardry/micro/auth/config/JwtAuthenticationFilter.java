package ru.itwizardry.micro.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey jwtSecretKey;

    public JwtAuthenticationFilter(String jwtSecret) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (jwt == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT token");
                return;
            }

            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            if (roles == null || roles.isEmpty()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "No roles in token");
                return;
            }

            var authorities = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            var authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException ex) {
            logger.error("JWT token expired", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
            return;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
            return;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("JWT validation error", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}