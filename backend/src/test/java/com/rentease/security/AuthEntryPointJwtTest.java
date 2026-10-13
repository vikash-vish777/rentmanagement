package com.rentease.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class AuthEntryPointJwtTest {

    @Test
    void commenceWritesUnauthorizedJsonResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/private");
        MockHttpServletResponse response = new MockHttpServletResponse();

        new AuthEntryPointJwt().commence(request, response, new BadCredentialsException("Invalid credentials"));

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("application/json", response.getContentType());
        String body = response.getContentAsString();
        assertTrue(body.contains("Unauthorized"));
        assertTrue(body.contains("Invalid credentials"));
        assertTrue(body.contains("/api/private"));
    }
}
