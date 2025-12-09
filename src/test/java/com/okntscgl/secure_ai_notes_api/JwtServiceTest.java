package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();

        // SECRET — en az 32 byte (HMAC için zorunlu)
        ReflectionTestUtils.setField(jwtService, "jwtSecret",
                "12345678901234567890123456789012");

        // Normal expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 1000L * 60); // 1 minute

        ReflectionTestUtils.setField(jwtService, "refreshJwtExpirationMs", 1000L * 60 * 60);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken("john");

        assertNotNull(token);
        assertEquals("john", jwtService.extractUsernameFromToken(token));
    }

    @Test
    void extractTokenFromHeader_shouldReturnToken() {
        String header = "Bearer abc.def.xyz";
        String result = jwtService.extractTokenFromHeader(header);

        assertEquals("abc.def.xyz", result);
    }

    @Test
    void extractTokenFromHeader_shouldReturnNullWhenInvalid() {
        assertNull(jwtService.extractTokenFromHeader("Token xyz"));
        assertNull(jwtService.extractTokenFromHeader(null));
    }

    @Test
    void token_shouldBeValidBeforeExpiration() {
        String token = jwtService.generateToken("john");

        var user = org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        org.mockito.Mockito.when(user.getUsername()).thenReturn("john");

        assertTrue(jwtService.isValidToken(token, user));
    }

    @Test
    void token_shouldBeExpired() throws InterruptedException {
        // Token’ı hemen süresi dolacak şekilde ayarla
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 1L);

        String token = jwtService.generateToken("john");

        // expiration geçmesi için 10ms bekle
        Thread.sleep(10);

        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsernameFromToken(token));
    }

    @Test
    void malformedToken_shouldThrowException() {
        String badToken = "this.is.not.jwt";

        assertThrows(Exception.class, () ->
                jwtService.extractUsernameFromToken(badToken));
    }
}
