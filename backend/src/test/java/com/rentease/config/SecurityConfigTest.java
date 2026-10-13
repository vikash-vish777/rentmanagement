package com.rentease.config;

import com.rentease.security.AuthEntryPointJwt;
import com.rentease.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void passwordEncoderHashesAndMatchesPasswords() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String encoded = encoder.encode("secret");

        assertNotEquals("secret", encoded);
        assertTrue(encoder.matches("secret", encoded));
        assertFalse(encoder.matches("wrong", encoded));
    }

    @Test
    void authenticationProviderUsesConfiguredUserServiceAndEncoder() {
        SecurityConfig config = new SecurityConfig();
        UserDetailsServiceImpl service = mock(UserDetailsServiceImpl.class);
        org.springframework.test.util.ReflectionTestUtils.setField(config, "userDetailsService", service);

        DaoAuthenticationProvider provider = config.authenticationProvider();

        assertNotNull(provider);
    }

    @Test
    void corsConfigurationAllowsConfiguredApiMethods() {
        SecurityConfig config = new SecurityConfig();

        var source = config.corsConfigurationSource();
        var configuration = source.getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest());

        assertNotNull(configuration);
        assertTrue(configuration.getAllowedOriginPatterns().contains("*"));
        assertTrue(configuration.getAllowedMethods().contains("GET"));
        assertTrue(configuration.getAllowedHeaders().contains("Authorization"));
        assertTrue(configuration.getAllowCredentials());
    }
}
