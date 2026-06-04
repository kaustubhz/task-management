package com.management.tasks.config;

import com.management.tasks.security.CustomOAuth2UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Minimal test security configuration.
 * Provides mocks for OAuth2-related beans to prevent external network calls
 * during tests.
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return Mockito.mock(CustomOAuth2UserService.class);
    }
}
