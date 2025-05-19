package ru.itwizardry.micro.common.testutils;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextTestUtils {

    public static void mockSecurityContext(String userId, Claims claims) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, claims);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}