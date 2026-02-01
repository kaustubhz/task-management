package com.management.tasks.mapper;

import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TaskMapperImpl Unit Tests")
class TaskMapperImplTest {

    private TaskMapperImpl taskMapper;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("should map list of tasks to list of task responses")
    void map_WithValidTasks_ShouldReturnTaskResponses() {
        // Arrange
        Task task1 = Task.builder()
                .id(1L)
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .dueDate(now.plusDays(7))
                .createdAt(now)
                .updatedAt(now)
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .dueDate(now.plusDays(14))
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<Task> tasks = List.of(task1, task2);

        // Act
        List<TaskResponse> result = taskMapper.map(tasks);

        // Assert
        assertThat(result).hasSize(2);

        TaskResponse response1 = result.get(0);
        assertThat(response1.id()).isEqualTo(1L);
        assertThat(response1.title()).isEqualTo("Task 1");
        assertThat(response1.description()).isEqualTo("Description 1");
        assertThat(response1.status()).isEqualTo(TaskStatus.TODO);
        assertThat(response1.priority()).isEqualTo(TaskPriority.LOW);
        assertThat(response1.dueDate()).isEqualTo(now.plusDays(7));

        TaskResponse response2 = result.get(1);
        assertThat(response2.id()).isEqualTo(2L);
        assertThat(response2.title()).isEqualTo("Task 2");
        assertThat(response2.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response2.priority()).isEqualTo(TaskPriority.HIGH);
    }

    @Test
    @DisplayName("should return null when input is null")
    void map_WithNullInput_ShouldReturnNull() {
        // Act
        List<TaskResponse> result = taskMapper.map(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return empty list when input is empty")
    void map_WithEmptyList_ShouldReturnEmptyList() {
        // Act
        List<TaskResponse> result = taskMapper.map(Collections.emptyList());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should handle task with null optional fields")
    void map_WithNullOptionalFields_ShouldMapCorrectly() {
        // Arrange
        Task task = Task.builder()
                .id(1L)
                .title("Minimal Task")
                .description(null)
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<Task> tasks = List.of(task);

        // Act
        List<TaskResponse> result = taskMapper.map(tasks);

        // Assert
        assertThat(result).hasSize(1);
        TaskResponse response = result.get(0);
        assertThat(response.title()).isEqualTo("Minimal Task");
        assertThat(response.description()).isNull();
        assertThat(response.dueDate()).isNull();
    }
}
