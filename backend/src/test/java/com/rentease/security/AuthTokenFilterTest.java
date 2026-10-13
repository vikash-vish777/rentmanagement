package com.rentease.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validBearerTokenSetsAuthenticationAndContinues() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserDetailsServiceImpl userService = mock(UserDetailsServiceImpl.class);
        UserDetailsImpl details = new UserDetailsImpl(1L, "Asha", "asha@example.com", "555", "secret", true, java.util.List.of());
        when(jwtUtils.validateJwtToken("token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("token")).thenReturn("asha@example.com");
        when(userService.loadUserByUsername("asha@example.com")).thenReturn(details);

        ExposedAuthTokenFilter filter = new ExposedAuthTokenFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(filter, "userDetailsService", userService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        FilterChain chain = mock(FilterChain.class);

        filter.run(request, new MockHttpServletResponse(), chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("asha@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(any(), any());
    }

    @Test
    void requestWithoutBearerTokenContinuesUnauthenticated() throws Exception {
        ExposedAuthTokenFilter filter = new ExposedAuthTokenFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", mock(JwtUtils.class));
        ReflectionTestUtils.setField(filter, "userDetailsService", mock(UserDetailsServiceImpl.class));
        FilterChain chain = mock(FilterChain.class);

        filter.run(new MockHttpServletRequest(), new MockHttpServletResponse(), chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(any(), any());
    }

    @Test
    void invalidBearerTokenDoesNotSetAuthentication() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        when(jwtUtils.validateJwtToken("bad-token")).thenReturn(false);
        ExposedAuthTokenFilter filter = new ExposedAuthTokenFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(filter, "userDetailsService", mock(UserDetailsServiceImpl.class));

        filter.run(newRequest("bad-token"), new MockHttpServletResponse(), mock(FilterChain.class));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private MockHttpServletRequest newRequest(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    private static class ExposedAuthTokenFilter extends AuthTokenFilter {
        void run(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
            doFilterInternal(request, response, chain);
        }
    }
}
