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
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration"
})
@org.springframework.test.context.ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WithMockUser
@DisplayName("TaskController Integration Tests")
class TaskControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TaskServiceBusinessLogic taskServiceBusinessLogic;

        @MockitoBean
        private CustomOAuth2UserService customOAuth2UserService;

        @MockitoBean
        private ClientRegistrationRepository clientRegistrationRepository;

        private ObjectMapper objectMapper;
        private Task sampleTask;
        private TaskRequest sampleTaskRequest;
        private TaskUpdateRequest sampleTaskUpdateRequest;
        private TaskResponse sampleTaskResponse;
        private LocalDateTime now;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                now = LocalDateTime.now();

                sampleTask = Task.builder()
                                .id(1L)
                                .title("Test Task")
                                .description("Test Description")
                                .status(TaskStatus.TODO)
                                .priority(TaskPriority.MEDIUM)
                                .dueDate(now.plusDays(7))
                                .createdAt(now)
                                .updatedAt(now)
                                .build();

                sampleTaskRequest = new TaskRequest(
                                "Test Task",
                                "Test Description",
                                TaskStatus.TODO,
                                TaskPriority.MEDIUM,
                                now.plusDays(7));

                sampleTaskUpdateRequest = new TaskUpdateRequest(
                                "Updated Task",
                                "Updated Description",
                                TaskStatus.IN_PROGRESS,
                                TaskPriority.HIGH,
                                now.plusDays(14));

                sampleTaskResponse = new TaskResponse(
                                1L,
                                "Test Task",
                                "Test Description",
                                TaskStatus.TODO,
                                TaskPriority.MEDIUM,
                                now.plusDays(7),
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
                                        .andExpect(jsonPath("$.id").value(1))
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
                                        now.plusDays(7));

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
                                        now.plusDays(7));

                        // Act & Assert
                        mockMvc.perform(post("/api/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest)))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("GET /api/tasks")
        class GetTasksTests {

                @Test
                @DisplayName("should return 200 OK with list of tasks")
                void getTasks_ShouldReturn200WithList() throws Exception {
                        // Arrange
                        when(taskServiceBusinessLogic.fetchTasks()).thenReturn(List.of(sampleTaskResponse));

                        // Act & Assert
                        mockMvc.perform(get("/api/tasks"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$[0].id").value(1))
                                        .andExpect(jsonPath("$[0].title").value("Test Task"));
                }

                @Test
                @DisplayName("should return 200 OK with empty list when no tasks")
                void getTasks_WhenEmpty_ShouldReturn200WithEmptyList() throws Exception {
                        // Arrange
                        when(taskServiceBusinessLogic.fetchTasks()).thenReturn(Collections.emptyList());

                        // Act & Assert
                        mockMvc.perform(get("/api/tasks"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$").isEmpty());
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
                                        .andExpect(jsonPath("$.id").value(1))
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
                                        .id(1L)
                                        .title("Updated Task")
                                        .description("Updated Description")
                                        .status(TaskStatus.IN_PROGRESS)
                                        .priority(TaskPriority.HIGH)
                                        .dueDate(now.plusDays(14))
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
                                        .andExpect(jsonPath("$.id").value(1))
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
