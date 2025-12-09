package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.controller.HomeController;
import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import com.okntscgl.secure_ai_notes_api.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    private AiService aiService;
    private HomeController homeController;
    private OidcUser oidcUser;

    @BeforeEach
    void setUp() {
        aiService = mock(AiService.class);
        homeController = new HomeController(aiService);
        oidcUser = mock(OidcUser.class);
    }

    @Test
    void testHomeWithPrincipal() {
        when(oidcUser.getPreferredUsername()).thenReturn("testUser");
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(oidcUser.getFullName()).thenReturn("Test User");
        when(oidcUser.getAuthorities()).thenReturn(List.of());

        List<NoteRequestModel> notes = List.of(new NoteRequestModel());
        when(aiService.getNotesByUser("testUser")).thenReturn(notes);

        Map<String, Object> response = homeController.home(oidcUser);

        assertEquals("testUser", response.get("username"));
        assertEquals("test@example.com", response.get("email"));
        assertEquals("Test User", response.get("name"));
        assertEquals(notes, response.get("notes"));

        verify(aiService, times(1)).getNotesByUser("testUser");
    }

    @Test
    void testHomeWithoutPrincipal() {
        Map<String, Object> response = homeController.home(null);
        assertEquals(0, response.size());
        verifyNoInteractions(aiService);
    }

    @Test
    void testUserEndpoint() {
        when(oidcUser.getFullName()).thenReturn("Test User");
        Map<String, Object> response = homeController.user(oidcUser);
        assertEquals("Test User", response.get("name"));
    }

    @Test
    void testManagerEndpoint() {
        when(oidcUser.getFullName()).thenReturn("Test User");
        Map<String, Object> response = homeController.manager(oidcUser);
        assertEquals("Test User", response.get("name"));
    }

    @Test
    void testAdminEndpoint() {
        when(oidcUser.getFullName()).thenReturn("Test User");
        Map<String, Object> response = homeController.admin(oidcUser);
        assertEquals("Test User", response.get("name"));
    }
}
