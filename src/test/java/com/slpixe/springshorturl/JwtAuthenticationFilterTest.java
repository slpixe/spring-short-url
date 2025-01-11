package com.slpixe.springshorturl;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtUtil jwtUtil;
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userRepo = mock(UserRepo.class);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userRepo);
        SecurityContextHolder.clearContext(); // Clear security context
    }

    @Test
    void shouldAuthenticateWithValidToken() throws IOException, ServletException {
        // Arrange
        String validToken = "valid.jwt.token";
        String username = "testUser";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(jwtUtil.validateToken(validToken, username)).thenReturn(true);
        when(userRepo.findByUsername(username)).thenReturn(java.util.Optional.of(new UserModel()));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWithInvalidToken() throws IOException, ServletException {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Mock behavior for an invalid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtil.extractUsername(invalidToken)).thenReturn(null); // Simulate invalid token (no username)
        when(jwtUtil.validateToken(invalidToken, null)).thenReturn(false); // Simulate invalid token (validation fails)

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication()); // No authentication should be set
        verify(filterChain).doFilter(request, response); // Filter chain should continue
    }

    @Test
    void shouldNotAuthenticateWithoutAuthorizationHeader() throws IOException, ServletException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipAuthenticationIfAlreadyAuthenticated() throws IOException, ServletException {
        // Arrange
        String token = "valid.jwt.token";
        String username = "testUser";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);

        // Set an existing authentication
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, null)
        );

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }
}
