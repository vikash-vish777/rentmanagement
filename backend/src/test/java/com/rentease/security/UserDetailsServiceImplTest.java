package com.rentease.security;

import com.rentease.entity.Role;
import com.rentease.entity.User;
import com.rentease.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new User("Asha", "asha@example.com", "555-0100", "secret", Role.ROLE_TENANT);
        activeUser.setId(42L);
    }

    @Test
    void loadUserByUsernameReturnsActiveUserDetails() {
        when(userRepository.findByEmail("asha@example.com")).thenReturn(Optional.of(activeUser));

        UserDetailsImpl details = (UserDetailsImpl) service.loadUserByUsername("asha@example.com");

        assertEquals("asha@example.com", details.getUsername());
        verify(userRepository).findByEmail("asha@example.com");
    }

    @Test
    void loadUserByUsernameRejectsMissingUser() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@example.com"));

        assertTrue(exception.getMessage().contains("missing@example.com"));
    }

    @Test
    void loadUserByUsernameRejectsInactiveUser() {
        activeUser.setActive(false);
        when(userRepository.findByEmail(activeUser.getEmail())).thenReturn(Optional.of(activeUser));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(activeUser.getEmail()));

        assertTrue(exception.getMessage().contains("deactivated/blocked"));
    }
}
