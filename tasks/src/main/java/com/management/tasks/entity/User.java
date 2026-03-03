package com.management.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    private Set<String> roles;

    @Builder.Default
    private Boolean enabled = true;

    private String provider; // "local", "keycloak", "google", "github"

    private String providerId; // OAuth2 provider's user ID

    private String imageUrl; // Profile picture URL

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
