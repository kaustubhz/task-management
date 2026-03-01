package com.management.tasks.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Minimal test security configuration.
 * Does NOT declare SecurityFilterChain or HttpSecurity-dependent beans,
 * so it can be safely imported by both @DataJpaTest and @WebMvcTest slices.
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
