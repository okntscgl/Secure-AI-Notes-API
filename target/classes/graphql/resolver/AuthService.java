package com.okntscgl.secure_ai_notes_api.service;

import com.okntscgl.secure_ai_notes_api.dto.LoginRequest;
import com.okntscgl.secure_ai_notes_api.dto.RegisterRequest;
import com.okntscgl.secure_ai_notes_api.dto.TokenPair;
import com.okntscgl.secure_ai_notes_api.model.Role;
import com.okntscgl.secure_ai_notes_api.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // ===================== REGISTER =====================
    public TokenPair register(RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.ROLE_USER);

        userService.createUser(user);

        // Kayıt sonrası token üretimi
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new TokenPair(accessToken, refreshToken);
    }

    // ===================== LOGIN =====================
    public TokenPair login(LoginRequest request) {
        // Authentication
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userService.findByUsername(request.getUsername());

        // Token üretimi
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new TokenPair(accessToken, refreshToken);
    }

    // ===================== REFRESH TOKEN =====================
    public TokenPair refreshToken(String refreshToken) {
        String username = jwtService.extractUsernameFromToken(refreshToken);

        User user = userService.findByUsername(username);

        if (!jwtService.isValidToken(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String accessToken = jwtService.generateToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        return new TokenPair(accessToken, newRefreshToken);
    }
}
