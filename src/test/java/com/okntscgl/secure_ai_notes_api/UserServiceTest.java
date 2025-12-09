package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import com.okntscgl.secure_ai_notes_api.repository.UserRepository;
import com.okntscgl.secure_ai_notes_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldSaveUser() {
        User user = new User();
        user.setUsername("john");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("john", result.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId("123");

        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById("123");

        assertTrue(result.isPresent());
        assertEquals("123", result.get().getId());
    }

    @Test
    void updateUser_shouldUpdateFields() {
        User existing = new User();
        existing.setId("123");
        existing.setName("Old");
        existing.setEmail("old@mail.com");

        User updated = new User();
        updated.setName("New Name");
        updated.setEmail("new@mail.com");
        updated.setUsername("newUser");
        updated.setRole(Role.ROLE_ADMIN);

        when(userRepository.findById("123")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUser("123", updated);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("newUser", result.getUsername());
        assertEquals(Role.ROLE_ADMIN, result.getRole());
    }

    @Test
    void updateUser_shouldReturnNull_whenNotFound() {
        when(userRepository.findById("123")).thenReturn(Optional.empty());

        User result = userService.updateUser("123", new User());

        assertNull(result);
    }

    @Test
    void deleteUser_shouldReturnTrue_whenExists() {
        User user = new User();
        user.setId("123");

        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser("123");

        assertTrue(result);
        verify(userRepository, times(1)).deleteById("123");
    }

    @Test
    void deleteUser_shouldReturnFalse_whenNotExists() {
        when(userRepository.findById("123")).thenReturn(Optional.empty());

        boolean result = userService.deleteUser("123");

        assertFalse(result);
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@mail.com");

        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void getUsersByRole_shouldReturnList() {
        User user = new User();
        user.setRole(Role.ROLE_ADMIN);

        when(userRepository.findByRole(Role.ROLE_ADMIN)).thenReturn(List.of(user));

        List<User> result = userService.getUsersByRole(Role.ROLE_ADMIN);

        assertEquals(1, result.size());
        assertEquals(Role.ROLE_ADMIN, result.get(0).getRole());
    }
}
