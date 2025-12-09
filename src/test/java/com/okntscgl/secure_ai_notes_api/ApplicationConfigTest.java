package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.config.ApplicationConfig;
import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import com.okntscgl.secure_ai_notes_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationConfigTest {

    private UserRepository userRepository;
    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        applicationConfig = new ApplicationConfig(userRepository);
    }

    @Test
    void testUserDetailsServiceReturnsUserDetails() {
        // Mock user
        User mockUser = new User();
        mockUser.setUsername("john");
        mockUser.setPassword("password123");
        mockUser.setRole(Role.ROLE_ADMIN);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(mockUser));

        UserDetailsService uds = applicationConfig.userDetailsService();
        UserDetails userDetails = uds.loadUserByUsername("john");

        assertEquals("john", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    void testUserDetailsServiceThrowsExceptionIfUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserDetailsService uds = applicationConfig.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> uds.loadUserByUsername("unknown"));

        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
