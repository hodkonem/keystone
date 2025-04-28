package ru.itwizardry.micro.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itwizardry.micro.auth.util.JwtService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private final List<String> permitAllEndpoints = List.of("/auth/register", "/auth/login");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtService, permitAllEndpoints);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldNotFilter_PermitAllEndpoints() {
        request.setRequestURI("/auth/register");
        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void doFilterInternal_ValidJwt_SetsAuthentication() throws Exception {
        Claims mockClaims = mock(Claims.class);
        when(jwtService.validateAndExtractClaims(anyString())).thenReturn(mockClaims);
        when(jwtService.extractUsername(mockClaims)).thenReturn("user");
        when(jwtService.extractAuthorities(mockClaims))
                .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));

        request.addHeader("Authorization", "Bearer valid.jwt");

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).validateAndExtractClaims("valid.jwt");
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_InvalidJwt_Returns401() throws Exception {
        when(jwtService.validateAndExtractClaims(anyString()))
                .thenThrow(new JwtException("Invalid token"));

        request.addHeader("Authorization", "Bearer invalid.token");
        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
    }

    @Test
    void doFilterInternal_MissingJwt_Returns401() throws Exception {
        filter.doFilterInternal(request, response, filterChain);
        assertEquals(401, response.getStatus());
    }
}