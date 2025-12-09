package com.okntscgl.secure_ai_notes_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    // Kullanıcı frontend olmadan token isteyecek
    @GetMapping("/login-info")
    public Map<String, String> loginInfo() {
        // Burada frontend yok, sadece yönlendirme veya bilgi JSON olarak dönebilir
        return Map.of(
                "message", "Kullanıcı Keycloak üzerinden login olmalı",
                "loginUrl", "/oauth2/authorization/keycloak"
        );
    }
}
