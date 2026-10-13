package com.rentease.security;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String SECRET = "RentEaseSuperSecretKeyWithAtLeast256BitsOfEntropyForHMACSHA256SecurityAlgorithm!";
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60_000);
    }

    @Test
    void tokenRoundTripReturnsUsername() {
        UserDetailsImpl principal = new UserDetailsImpl(7L, "Asha", "asha@example.com", "555", "secret", true, java.util.List.of());
        String token = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        assertEquals("asha@example.com", jwtUtils.getUserNameFromJwtToken(token));
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void malformedTokenIsRejected() {
        assertFalse(jwtUtils.validateJwtToken("not-a-jwt"));
    }

    @Test
    void tokenSignedWithDifferentKeyIsRejected() {
        UserDetailsImpl principal = new UserDetailsImpl(7L, "Asha", "asha@example.com", "555", "secret", true, java.util.List.of());
        String token = io.jsonwebtoken.Jwts.builder()
                .subject(principal.getUsername())
                .signWith(Keys.hmacShaKeyFor("AnotherSecretKeyWithAtLeast256BitsOfEntropyForHMACSHA256SecurityAlgorithm!".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }
}
