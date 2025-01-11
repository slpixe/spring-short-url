package com.slpixe.springshorturl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        jwtConfig = Mockito.mock(JwtConfig.class);
        when(jwtConfig.getJwtSecret()).thenReturn("YourSecretKeyHereFjfiewojfioewjfewjifejwofjewiofjweoifw"); // Use a 32-byte secret
        when(jwtConfig.getJwtExpiration()).thenReturn(3600000L); // 1 hour
        jwtUtil = new JwtUtil(jwtConfig);
    }

    @Test
    void testGenerateToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        String extractedUsername = Jwts.parserBuilder()
                .setSigningKey(jwtUtil.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUsername() {
        String username = "testUser";
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(jwtUtil.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertTrue(jwtUtil.validateToken(token, username));
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertFalse(jwtUtil.validateToken(token, "invalidUser"));
    }

    @Test
    void testValidateToken_ExpiredToken() {
        when(jwtConfig.getJwtExpiration()).thenReturn(-1000L); // Expired token
        String token = jwtUtil.generateToken("testUser");

        assertFalse(jwtUtil.validateToken(token, "testUser"));
    }

    @Test
    void testIsTokenExpired() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired token
                .signWith(jwtUtil.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtil.validateToken(token, "testUser"));
    }
}
