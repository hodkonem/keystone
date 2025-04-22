package ru.itwizardry.micro.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itwizardry.micro.auth.util.JwtUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey jwtSecretKey;
    private final List<String> permitAllEndpoints;

    public JwtAuthenticationFilter(String jwtSecret, List<String> permitAllEndpoints) {
        this.jwtSecretKey = JwtUtils.getSecretKey(jwtSecret);
        this.permitAllEndpoints = permitAllEndpoints;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return permitAllEndpoints.stream().anyMatch(path::startsWith);
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

            Claims claims = JwtUtils.validateAndExtractClaims(jwt, jwtSecretKey);
            String username = JwtUtils.extractUsername(claims);
            var authorities = JwtUtils.extractAuthorities(claims);

            var authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
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