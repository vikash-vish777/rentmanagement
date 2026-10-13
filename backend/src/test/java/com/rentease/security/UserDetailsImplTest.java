package com.rentease.security;

import com.rentease.entity.Role;
import com.rentease.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void buildMapsUserFieldsAndRoleAuthority() {
        User user = new User("Asha", "asha@example.com", "555-0100", "secret", Role.ROLE_TENANT);
        user.setId(42L);

        UserDetailsImpl details = UserDetailsImpl.build(user);

        assertEquals(42L, details.getId());
        assertEquals("Asha", details.getName());
        assertEquals("asha@example.com", details.getUsername());
        assertEquals("555-0100", details.getPhone());
        assertEquals("secret", details.getPassword());
        assertEquals("ROLE_TENANT", details.getAuthorities().iterator().next().getAuthority());
        assertTrue(details.isEnabled());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isCredentialsNonExpired());
    }

    @Test
    void inactiveUserIsDisabledAndLocked() {
        UserDetailsImpl details = new UserDetailsImpl(1L, "Name", "name@example.com", "555", "secret", false, java.util.List.of());

        assertFalse(details.isEnabled());
        assertFalse(details.isAccountNonLocked());
    }

    @Test
    void equalityUsesUserId() {
        UserDetailsImpl first = new UserDetailsImpl(1L, "First", "first@example.com", "1", "p", true, java.util.List.of());
        UserDetailsImpl second = new UserDetailsImpl(1L, "Second", "second@example.com", "2", "q", false, java.util.List.of());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
    }
}
