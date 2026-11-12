package com.management.tasks.config;

import com.management.tasks.security.CustomOAuth2UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
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
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    public org.springframework.cache.CacheManager cacheManager() {
        return new org.springframework.cache.support.NoOpCacheManager();
    }

    @Bean
    public org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails mongoConnectionDetails(
            org.springframework.beans.factory.ObjectProvider<de.flapdoodle.embed.mongo.config.Net> netProvider) {
        return () -> {
            de.flapdoodle.embed.mongo.config.Net net = netProvider.getIfAvailable();
            if (net != null) {
                try {
                    return new com.mongodb.ConnectionString("mongodb://" + net.getServerAddress().getHostAddress() + ":" + net.getPort() + "/testdb");
                } catch (Exception e) {
                    return new com.mongodb.ConnectionString("mongodb://localhost:" + net.getPort() + "/testdb");
                }
            }
            return new com.mongodb.ConnectionString("mongodb://localhost:27017/testdb");
        };
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return Mockito.mock(CustomOAuth2UserService.class);
    }
}
