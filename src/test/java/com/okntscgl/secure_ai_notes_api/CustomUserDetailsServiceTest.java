package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import com.okntscgl.secure_ai_notes_api.repository.UserRepository;
import com.okntscgl.secure_ai_notes_api.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setId("123");
        user.setUsername("john");
        user.setPassword("pass123");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        var result = customUserDetailsService.loadUserByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("pass123", result.getPassword());

        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("ghost")
        );

        verify(userRepository, times(1)).findByUsername("ghost");
    }
}
