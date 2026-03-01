package com.management.tasks.config;

import com.management.tasks.entity.Role;
import com.management.tasks.entity.RoleName;
import com.management.tasks.entity.User;
import com.management.tasks.repository.RoleRepository;
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

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing default roles and users...");

        // Create roles if they don't exist
        Role userRole = createRoleIfNotExists(RoleName.ROLE_USER);
        Role managerRole = createRoleIfNotExists(RoleName.ROLE_MANAGER);
        Role adminRole = createRoleIfNotExists(RoleName.ROLE_ADMIN);

        // Create default users if they don't exist
        createUserIfNotExists("user", "user@example.com", "password123", Set.of(userRole));
        createUserIfNotExists("manager", "manager@example.com", "password123", Set.of(userRole, managerRole));
        createUserIfNotExists("admin", "admin@example.com", "password123", Set.of(userRole, managerRole, adminRole));

        log.info("Data initialization completed.");
    }

    private Role createRoleIfNotExists(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name(roleName)
                            .build();
                    Role savedRole = roleRepository.save(role);
                    log.info("Created role: {}", roleName);
                    return savedRole;
                });
    }

    private void createUserIfNotExists(String username, String email, String password, Set<Role> roles) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .enabled(true)
                    .roles(roles)
                    .build();
            userRepository.save(user);
            log.info("Created user: {} with email: {}", username, email);
        }
    }
}
