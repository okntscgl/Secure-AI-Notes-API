package com.okntscgl.secure_ai_notes_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppConfigTest {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
        // Spring context yüklendi mi ve JwtDecoder bean’i oluşturuldu mu kontrol ediyoruz
        assertNotNull(jwtDecoder, "JwtDecoder bean should be created");
    }
}
