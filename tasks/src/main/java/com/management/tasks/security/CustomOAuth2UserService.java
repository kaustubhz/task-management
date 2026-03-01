package com.management.tasks.security;

import com.management.tasks.entity.Role;
import com.management.tasks.entity.RoleName;
import com.management.tasks.entity.User;
import com.management.tasks.repository.RoleRepository;
import com.management.tasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            org.springframework.security.oauth2.core.OAuth2Error error = new org.springframework.security.oauth2.core.OAuth2Error(
                    "oauth2_processing_error", "Error processing OAuth2 user: " + ex.getMessage(), null);
            throw new OAuth2AuthenticationException(error);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // Extract user info from OAuth2 provider
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String username = oAuth2User.getAttribute("preferred_username");

        if (email == null || email.isEmpty()) {
            org.springframework.security.oauth2.core.OAuth2Error error = new org.springframework.security.oauth2.core.OAuth2Error(
                    "email_not_found", "Email not found from OAuth2 provider", null);
            throw new OAuth2AuthenticationException(error);
        }

        log.info("Processing OAuth2 user - Provider: {}, Email: {}", registrationId, email);

        // Find or create user
        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, registrationId, providerId))
                .orElseGet(() -> createNewUser(registrationId, providerId, email, username, name));

        return new DefaultOAuth2User(
                getAuthorities(user),
                oAuth2User.getAttributes(),
                "sub");
    }

    private User updateExistingUser(User user, String provider, String providerId) {
        log.info("Updating existing user: {}", user.getEmail());
        user.setProvider(provider);
        user.setProviderId(providerId);
        return userRepository.save(user);
    }

    private User createNewUser(String provider, String providerId, String email,
            String username, String name) {
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User newUser = User.builder()
                .username(username != null ? username : email.split("@")[0])
                .email(email)
                .password(null) // No password for OAuth2 users
                .provider(provider)
                .providerId(providerId)
                .enabled(true)
                .roles(Set.of(userRole))
                .build();

        log.info("Creating new OAuth2 user: {}", email);
        return userRepository.save(newUser);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
    }
}
