package com.management.tasks.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.management.tasks.dto.request.TaskRequest;
import com.management.tasks.dto.request.TaskUpdateRequest;
import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import com.management.tasks.services.TaskServiceBusinessLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import com.management.tasks.security.CustomOAuth2UserService;

import org.springframework.context.annotation.Import;
import com.management.tasks.config.TestSecurityConfig;

@WebMvcTest(controllers = TaskController.class, properties = {
                "spring.autoconfigure.exclude=" +
                                "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
                                "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,"
                                +
                                "org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration,"
                                +
                                "org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration,"
                                +
                                "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,"
                                +
                                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,"
                                +
                                "org.springframework.boot.docker.compose.core.DockerComposeAutoConfiguration"
})
@org.springframework.test.context.ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WithMockUser
@DisplayName("TaskController Integration Tests")
class TaskControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TaskServiceBusinessLogic taskServiceBusinessLogic;

        @MockBean
        private CustomOAuth2UserService customOAuth2UserService;

        @MockBean
        private ClientRegistrationRepository clientRegistrationRepository;

        private ObjectMapper objectMapper;
        private Task sampleTask;
        private TaskRequest sampleTaskRequest;
        private TaskUpdateRequest sampleTaskUpdateRequest;
        private TaskResponse sampleTaskResponse;
        private LocalDateTime now;
        private OffsetDateTime nowOffset;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                now = LocalDateTime.now();
                nowOffset = OffsetDateTime.now();

                sampleTask = Task.builder()
                                .id("1")
                                .title("Test Task")
                                .description("Test Description")
                                .status(TaskStatus.TODO)
                                .priority(TaskPriority.MEDIUM)
                                .dueDate(nowOffset.plusDays(7))
                                .createdAt(now)
                                .updatedAt(now)
                                .build();

                sampleTaskRequest = new TaskRequest(
                                "Test Task",
                                "Test Description",
                                TaskStatus.TODO,
                                TaskPriority.MEDIUM,
                                nowOffset.plusDays(7));

                sampleTaskUpdateRequest = new TaskUpdateRequest(
                                "Updated Task",
                                "Updated Description",
                                TaskStatus.IN_PROGRESS,
                                TaskPriority.HIGH,
                                nowOffset.plusDays(14));

                sampleTaskResponse = new TaskResponse(
                                "1",
                                "Test Task",
                                "Test Description",
                                TaskStatus.TODO,
                                TaskPriority.MEDIUM,
                                nowOffset.plusDays(7),
                                now,
                                now);
        }

        @Nested
        @DisplayName("POST /api/tasks")
        class CreateTaskTests {

                @Test
                @DisplayName("should return 201 Created with task response")
                void createTask_ShouldReturn201Created() throws Exception {
                        // Arrange
                        when(taskServiceBusinessLogic.createTask(any(TaskRequest.class))).thenReturn(sampleTask);

                        // Act & Assert
                        mockMvc.perform(post("/api/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(sampleTaskRequest)))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id").value("1"))
                                        .andExpect(jsonPath("$.title").value("Test Task"))
                                        .andExpect(jsonPath("$.status").value("TODO"))
                                        .andExpect(jsonPath("$.priority").value("MEDIUM"));
                }

                @Test
                @DisplayName("should return 400 Bad Request when title is blank")
                void createTask_WithBlankTitle_ShouldReturn400() throws Exception {
                        // Arrange
					TaskRequest invalidRequest = new TaskRequest(
							"",
							"Description",
							TaskStatus.TODO,
							TaskPriority.MEDIUM,
							nowOffset.plusDays(7));
                        // In unit test mode, @Valid is triggered by MockMvc.
                        // If it bypasses validation (which shouldn't happen with @Valid),
                        // we mock it to return null to test our controller's safety check.
                        when(taskServiceBusinessLogic.createTask(any(TaskRequest.class))).thenReturn(null);

                        // Act & Assert
                        mockMvc.perform(post("/api/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("should return 400 Bad Request when status is null")
                void createTask_WithNullStatus_ShouldReturn400() throws Exception {
                        // Arrange
					TaskRequest invalidRequest = new TaskRequest(
							"Valid Title",
							"Description",
							null,
							TaskPriority.MEDIUM,
							nowOffset.plusDays(7));
                        when(taskServiceBusinessLogic.createTask(any(TaskRequest.class))).thenReturn(null);

                        // Act & Assert
                        mockMvc.perform(post("/api/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest)))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("GET /api/tasks")
        @WithMockUser(roles = "MANAGER")
        class GetTasksTests {

                @Test
                @DisplayName("should return 200 OK with list of tasks")
                void getTasks_ShouldReturn200WithList() throws Exception {
                        // Arrange
                        org.springframework.data.domain.Page<TaskResponse> page = new org.springframework.data.domain.PageImpl<>(
                                        List.of(sampleTaskResponse));
                        when(taskServiceBusinessLogic.findAllTasksByFilters(any(), any(), anyInt(), anyInt(),
                                        anyString(), anyString()))
                                        .thenReturn(page);

                        // Act & Assert
                        mockMvc.perform(get("/api/tasks"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.content").isArray())
                                        .andExpect(jsonPath("$.content[0].id").value("1"))
                                        .andExpect(jsonPath("$.content[0].title").value("Test Task"));
                }

                @Test
                @DisplayName("should return 200 OK with empty list when no tasks")
                void getTasks_WhenEmpty_ShouldReturn200WithEmptyList() throws Exception {
                        // Arrange
                        org.springframework.data.domain.Page<TaskResponse> emptyPage = new org.springframework.data.domain.PageImpl<>(
                                        Collections.emptyList());
                        when(taskServiceBusinessLogic.findAllTasksByFilters(any(), any(), anyInt(), anyInt(),
                                        anyString(), anyString()))
                                        .thenReturn(emptyPage);

                        // Act & Assert
                        mockMvc.perform(get("/api/tasks"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.content").isArray())
                                        .andExpect(jsonPath("$.content").isEmpty());
                }
        }

        @Nested
        @DisplayName("GET /api/tasks/{id}")
        class GetTaskByIdTests {

                @Test
                @DisplayName("should return 200 OK when task exists")
                void getTaskById_WhenExists_ShouldReturn200() throws Exception {
                        // Arrange
                        when(taskServiceBusinessLogic.getTaskById("1")).thenReturn(sampleTaskResponse);

                        // Act & Assert
                        mockMvc.perform(get("/api/tasks/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value("1"))
                                        .andExpect(jsonPath("$.title").value("Test Task"))
                                        .andExpect(jsonPath("$.status").value("TODO"));
                }

                @Test
                @DisplayName("should return 404 when task not found")
                void getTaskById_WhenNotExists_ShouldThrowException() throws Exception {
                        // Arrange
                        when(taskServiceBusinessLogic.getTaskById("999"))
                                        .thenThrow(new com.management.tasks.exceptions.TaskNotFoundException("999"));

                        // Act & Assert - GlobalExceptionHandler returns 404
                        mockMvc.perform(get("/api/tasks/999"))
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        @DisplayName("PUT /api/tasks/{id}")
        class UpdateTaskTests {

                @Test
                @DisplayName("should return 200 OK with updated task")
                void updateTask_ShouldReturn200WithUpdatedTask() throws Exception {
                        // Arrange
                        Task updatedTask = Task.builder()
                                        .id("1")
                                        .title("Updated Task")
                                        .description("Updated Description")
                                         .status(TaskStatus.IN_PROGRESS)
                                         .priority(TaskPriority.HIGH)
                                         .dueDate(nowOffset.plusDays(14))
                                         .createdAt(now)
                                        .updatedAt(now)
                                        .build();

                        when(taskServiceBusinessLogic.updateTask(eq("1"), any(TaskUpdateRequest.class)))
                                        .thenReturn(updatedTask);

                        // Act & Assert
                        mockMvc.perform(put("/api/tasks/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(sampleTaskUpdateRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").value("1"))
                                        .andExpect(jsonPath("$.title").value("Updated Task"))
                                        .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                                        .andExpect(jsonPath("$.priority").value("HIGH"));
                }
        }

        @Nested
        @DisplayName("DELETE /api/tasks/{id}")
        class DeleteTaskTests {

                @Test
                @DisplayName("should return 204 No Content on successful delete")
                void deleteTask_ShouldReturn204NoContent() throws Exception {
                        // Arrange
                        doNothing().when(taskServiceBusinessLogic).deleteTask("1");

                        // Act & Assert
                        mockMvc.perform(delete("/api/tasks/1"))
                                        .andExpect(status().isNoContent());
                }
        }
}
