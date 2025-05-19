package ru.itwizardry.micro.common.testutils;

import io.jsonwebtoken.Claims;
import org.mockito.Mockito;

public class JwtTestUtils {

    public static Claims mockClaims(String userId) {
        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.getSubject()).thenReturn(userId);
        return claims;
    }
}