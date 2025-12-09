package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.controller.UserController;
import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import com.okntscgl.secure_ai_notes_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User();
        user1.setId("1");
        user1.setUsername("user1");
        user1.setEmail("user1@test.com");
        user1.setRole(Role.ROLE_USER);

        user2 = new User();
        user2.setId("2");
        user2.setUsername("user2");
        user2.setEmail("user2@test.com");
        user2.setRole(Role.ROLE_ADMIN);
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(user1)).thenReturn(user1);
        ResponseEntity<User> response = userController.createUser(user1);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user1, response.getBody());
        verify(userService, times(1)).createUser(user1);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById_Found() {
        when(userService.getUserById("1")).thenReturn(Optional.of(user1));

        ResponseEntity<User> response = userController.getUserById("1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user1, response.getBody());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById("3")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById("3");
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateUser_Found() {
        User updatedUser = new User();
        updatedUser.setId("1");
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@test.com");

        when(userService.updateUser("1", updatedUser)).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser("1", updatedUser);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedUser = new User();
        updatedUser.setId("3");

        when(userService.updateUser("3", updatedUser)).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser("3", updatedUser);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteUser_Found() {
        when(userService.deleteUser("1")).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser("1");
        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser("1");
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userService.deleteUser("3")).thenReturn(false);

        ResponseEntity<Void> response = userController.deleteUser("3");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetUserByEmail_Found() {
        when(userService.findByEmail("user1@test.com")).thenReturn(user1);

        ResponseEntity<User> response = userController.getUserByEmail("user1@test.com");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user1, response.getBody());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userService.findByEmail("unknown@test.com")).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByEmail("unknown@test.com");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetUsersByRole() {
        List<User> users = Arrays.asList(user1);
        when(userService.getUsersByRole(Role.ROLE_USER)).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getUsersByRole(Role.ROLE_USER);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(Role.ROLE_USER, response.getBody().get(0).getRole());
    }
}
