package com.management.tasks.config;

import com.management.tasks.entity.RoleName;
import com.management.tasks.entity.User;
import com.management.tasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing default roles and users...");


        // Create default users if they don't exist
        createUserIfNotExists("user", "user@example.com", "password123", Set.of(RoleName.ROLE_USER.name()));
        createUserIfNotExists("manager", "manager@example.com", "password123",
                Set.of(RoleName.ROLE_USER.name(), RoleName.ROLE_MANAGER.name()));
        createUserIfNotExists("admin", "admin@example.com", "password123",
                Set.of(RoleName.ROLE_USER.name(), RoleName.ROLE_MANAGER.name(), RoleName.ROLE_ADMIN.name()));

        log.info("Data initialization completed.");
    }


    private void createUserIfNotExists(String username, String email, String password, Set<String> roles) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .roles(roles)
                    .enabled(true)
                    .build();
            userRepository.save(user);
            log.info("Created user: {} with email: {}", username, email);
        }
    }
}
