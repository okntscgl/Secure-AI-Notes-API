package com.okntscgl.secure_ai_notes_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    
    @GetMapping("/login-info")
    public Map<String, String> loginInfo() {
        
        return Map.of(
                "message", "The user is required to authenticate through Keycloak.",
                "loginUrl", "/oauth2/authorization/keycloak"
        );
    }
}

