package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.controller.LoginController;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    void testLoginInfo() {
        Map<String, String> response = loginController.loginInfo();

        assertEquals("Kullanıcı Keycloak üzerinden login olmalı", response.get("message"));
        assertEquals("/oauth2/authorization/keycloak", response.get("loginUrl"));
    }
}
