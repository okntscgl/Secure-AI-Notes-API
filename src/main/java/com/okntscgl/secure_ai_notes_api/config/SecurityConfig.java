package com.okntscgl.secure_ai_notes_api.config;

import com.okntscgl.secure_ai_notes_api.filter.JwtAuthenticationFilter;
import com.okntscgl.secure_ai_notes_api.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomOidcUserService oidcUserService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsService userDetailsService,
                          ClientRegistrationRepository clientRegistrationRepository,
                          CustomOidcUserService oidcUserService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oidcUserService = oidcUserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        OidcClientInitiatedLogoutSuccessHandler logoutHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        logoutHandler.setPostLogoutRedirectUri("http://localhost:8080/");

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/api/auth/**").permitAll()
                        .requestMatchers("/user").hasAuthority("ROLE_USER")
                        .requestMatchers("/manager").hasAuthority("ROLE_MANAGER")
                        .requestMatchers("/admin").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/ai-notes/**").hasAuthority("ROLE_USER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                )
                .logout(logout -> logout.logoutSuccessHandler(logoutHandler));

        return http.build();
    }
}
