package com.okntscgl.secure_ai_notes_api.config;

import com.okntscgl.secure_ai_notes_api.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().name().replace("ROLE_", "")) // ROLE_ prefix Spring Security otomatik ekler
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
