package com.okntscgl.secure_ai_notes_api;

import com.okntscgl.secure_ai_notes_api.filter.JwtAuthenticationFilter;
import com.okntscgl.secure_ai_notes_api.service.CustomOidcUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfigTest.TestConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return Mockito.mock(UserDetailsService.class);
        }

        @Bean
        public CustomOidcUserService oidcUserService() {
            return Mockito.mock(CustomOidcUserService.class);
        }

        @Bean
        public org.springframework.security.oauth2.client.registration.ClientRegistrationRepository clientRegistrationRepository() {
            return Mockito.mock(org.springframework.security.oauth2.client.registration.ClientRegistrationRepository.class);
        }
    }

    @Test
    @WithMockUser(roles = "USER")
    void accessUserEndpointWithUserRole() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void accessAdminEndpointWithAdminRole() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void accessAdminEndpointWithUserRoleShouldFail() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }
}
