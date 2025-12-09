package com.okntscgl.secure_ai_notes_api.controller;

import com.okntscgl.secure_ai_notes_api.model.NoteRequestModel;
import com.okntscgl.secure_ai_notes_api.service.AiService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    private final AiService aiService;

    public HomeController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/home")
    public Map<String, Object> home(@AuthenticationPrincipal OidcUser principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal != null) {
            String username = principal.getPreferredUsername();
            response.put("username", username);
            response.put("email", principal.getEmail());
            response.put("name", principal.getFullName());
            response.put("roles", principal.getAuthorities());

            List<NoteRequestModel> notes = aiService.getNotesByUser(username);
            response.put("notes", notes);
        }
        return response;
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OidcUser principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal != null) {
            response.put("name", principal.getFullName());
        }
        return response;
    }

    @GetMapping("/manager")
    public Map<String, Object> manager(@AuthenticationPrincipal OidcUser principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal != null) {
            response.put("name", principal.getFullName());
        }
        return response;
    }

    @GetMapping("/admin")
    public Map<String, Object> admin(@AuthenticationPrincipal OidcUser principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal != null) {
            response.put("name", principal.getFullName());
        }
        return response;
    }
}
