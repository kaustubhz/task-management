package com.management.tasks.security;

import com.management.tasks.entity.Role;
import com.management.tasks.entity.RoleName;
import com.management.tasks.entity.User;
import com.management.tasks.repository.RoleRepository;
import com.management.tasks.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2UserService Unit Tests")
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    // We can't easily mock super.loadUser() from DefaultOAuth2UserService,
    // so we'll test the processOAuth2User logic through a subclass wrapper or
    // reflection,
    // or by mocking the dependencies of DefaultOAuth2UserService if needed.
    // For simplicity, we can test the logic directly by making a spy or extracting
    // the method,
    // but processOAuth2User is private. Let's make it package-private or use
    // reflection to test it,
    // OR test it indirectly if we override the loadUser.
    // Since we call super.loadUser(userRequest) which makes an actual HTTP request,
    // it's better to refactor or use a testable approach. Let's just create a
    // custom implementation or spy.
    // Actually, testing Spring Security's DefaultOAuth2UserService requires mocking
    // RestOperations or simply extracting the business logic to a separate public
    // method.

    // To make it testable without HTTP calls, let's test the public loadUser by
    // overriding super in a Spy
    // or we can just mock the parent method. Let's create a
    // TestableCustomOAuth2UserService.

    private TestableCustomOAuth2UserService testableService;

    private Role userRole;
    private ClientRegistration keycloakRegistration;
    private OAuth2AccessToken accessToken;

    @BeforeEach
    void setUp() {
        testableService = new TestableCustomOAuth2UserService(userRepository, roleRepository);

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.ROLE_USER);

        keycloakRegistration = ClientRegistration
                .withRegistrationId("keycloak")
                .clientId("task-management-client")
                .clientSecret("secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("http://localhost:8180/realms/task-management/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8180/realms/task-management/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8180/realms/task-management/protocol/openid-connect/userinfo")
                .userNameAttributeName("sub")
                .build();

        accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(3600));
    }

    private OAuth2UserRequest mockUserRequest() {
        return new OAuth2UserRequest(keycloakRegistration, accessToken);
    }

    private OAuth2User mockOAuth2User(String sub, String email, String username, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", sub);
        attributes.put("email", email);
        attributes.put("preferred_username", username);
        attributes.put("name", name);

        OAuth2User oAuth2User = mock(OAuth2User.class);
        lenient().when(oAuth2User.getAttributes()).thenReturn(attributes);
        lenient().when(oAuth2User.getAttribute("sub")).thenReturn(sub);
        lenient().when(oAuth2User.getAttribute("email")).thenReturn(email);
        lenient().when(oAuth2User.getAttribute("preferred_username")).thenReturn(username);
        lenient().when(oAuth2User.getAttribute("name")).thenReturn(name);
        return oAuth2User;
    }

    @Nested
    @DisplayName("New OAuth2 user creation")
    class NewUserCreation {

        @Test
        @DisplayName("should create a new User in DB when OAuth2 user is not found by email")
        void loadUser_NewUser_ShouldCreateAndSave() {
            // Arrange
            OAuth2UserRequest userRequest = mockUserRequest();
            OAuth2User mockOAuth2 = mockOAuth2User("keycloak-sub-123", "oauth@example.com", "oauth-user", "OAuth User");

            testableService.setMockedSuperUser(mockOAuth2);

            when(userRepository.findByEmail("oauth@example.com")).thenReturn(Optional.empty());
            when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));

            User savedUser = User.builder()
                    .id(10L)
                    .username("oauth-user")
                    .email("oauth@example.com")
                    .provider("keycloak")
                    .providerId("keycloak-sub-123")
                    .enabled(true)
                    .roles(Set.of(userRole))
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // Act
            OAuth2User result = testableService.loadUser(userRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getAttribute("sub").toString()).isEqualTo("keycloak-sub-123");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getEmail()).isEqualTo("oauth@example.com");
            assertThat(capturedUser.getUsername()).isEqualTo("oauth-user");
            assertThat(capturedUser.getProvider()).isEqualTo("keycloak");
            assertThat(capturedUser.getProviderId()).isEqualTo("keycloak-sub-123");
            assertThat(capturedUser.getPassword()).isNull();
        }
    }

    @Nested
    @DisplayName("Existing OAuth2 user merge")
    class ExistingUserMerge {

        @Test
        @DisplayName("should update provider info when user exists with same email")
        void loadUser_ExistingUser_ShouldUpdateProviderInfo() {
            // Arrange
            OAuth2UserRequest userRequest = mockUserRequest();
            OAuth2User mockOAuth2 = mockOAuth2User("keycloak-sub-123", "existing@example.com", "existing",
                    "Existing User");

            testableService.setMockedSuperUser(mockOAuth2);

            User existingUser = User.builder()
                    .id(5L)
                    .username("existing_local")
                    .email("existing@example.com")
                    .password("hashed_pass")
                    .provider(null)
                    .providerId(null)
                    .roles(Set.of(userRole))
                    .build();

            when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            // Act
            OAuth2User result = testableService.loadUser(userRequest);

            // Assert
            assertThat(result).isNotNull();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getEmail()).isEqualTo("existing@example.com");
            assertThat(capturedUser.getProvider()).isEqualTo("keycloak");
            assertThat(capturedUser.getProviderId()).isEqualTo("keycloak-sub-123");
            // Roles/password should be retained
            assertThat(capturedUser.getPassword()).isEqualTo("hashed_pass");
        }
    }

    @Nested
    @DisplayName("Error cases")
    class ErrorCases {

        @Test
        @DisplayName("should throw exception when email is missing")
        void loadUser_MissingEmail_ShouldThrowException() {
            // Arrange
            OAuth2UserRequest userRequest = mockUserRequest();
            OAuth2User mockOAuth2 = mockOAuth2User("keycloak-sub-123", null, "no-email", "No Email User");

            testableService.setMockedSuperUser(mockOAuth2);

            // Act & Assert
            assertThatThrownBy(() -> testableService.loadUser(userRequest))
                    .isInstanceOf(OAuth2AuthenticationException.class)
                    .hasMessageContaining("Email not found");

            verifyNoInteractions(userRepository);
        }
    }

    /**
     * Helper subclass to bypass super.loadUser() which makes an HTTP request
     */
    private static class TestableCustomOAuth2UserService extends CustomOAuth2UserService {
        private OAuth2User mockedSuperUser;

        public TestableCustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
            super(userRepository, roleRepository);
        }

        public void setMockedSuperUser(OAuth2User user) {
            this.mockedSuperUser = user;
        }

        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            // Bypass super.loadUser
            OAuth2User oAuth2User = this.mockedSuperUser;
            try {
                // Use reflection to call private method processOAuth2User
                java.lang.reflect.Method method = CustomOAuth2UserService.class.getDeclaredMethod("processOAuth2User",
                        OAuth2UserRequest.class, OAuth2User.class);
                method.setAccessible(true);
                return (OAuth2User) method.invoke(this, userRequest, oAuth2User);
            } catch (java.lang.reflect.InvocationTargetException e) {
                if (e.getCause() instanceof OAuth2AuthenticationException) {
                    throw (OAuth2AuthenticationException) e.getCause();
                }
                throw new OAuth2AuthenticationException("Error: " + e.getCause().getMessage());
            } catch (Exception e) {
                throw new OAuth2AuthenticationException("Error: " + e.getMessage());
            }
        }
    }
}
