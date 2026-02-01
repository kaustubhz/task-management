package com.management.tasks.services;

import com.management.tasks.dto.request.TaskRequest;
import com.management.tasks.dto.request.TaskUpdateRequest;
import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import com.management.tasks.mapper.TaskMapper;
import com.management.tasks.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskServiceBusinessLogic Unit Tests")
class TaskServiceBusinessLogicTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceBusinessLogic taskService;

    private Task sampleTask;
    private TaskRequest sampleTaskRequest;
    private TaskUpdateRequest sampleTaskUpdateRequest;
    private TaskResponse sampleTaskResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
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
    @DisplayName("createTask")
    class CreateTaskTests {

        @Test
        @DisplayName("should save and return the created task")
        void createTask_ShouldSaveAndReturnTask() {
            // Arrange
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // Act
            Task result = taskService.createTask(sampleTaskRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Task");
            assertThat(result.getDescription()).isEqualTo("Test Description");
            assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
            assertThat(result.getPriority()).isEqualTo(TaskPriority.MEDIUM);

            verify(taskRepository).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("fetchTasks")
    class FetchTasksTests {

        @Test
        @DisplayName("should return mapped task responses when tasks exist")
        void fetchTasks_ShouldReturnMappedTaskResponses() {
            // Arrange
            List<Task> tasks = List.of(sampleTask);
            List<TaskResponse> expectedResponses = List.of(sampleTaskResponse);

            when(taskRepository.findAll()).thenReturn(tasks);
            when(taskMapper.map(tasks)).thenReturn(expectedResponses);

            // Act
            List<TaskResponse> result = taskService.fetchTasks();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(0).title()).isEqualTo("Test Task");

            verify(taskRepository).findAll();
            verify(taskMapper).map(tasks);
        }

        @Test
        @DisplayName("should return empty list when no tasks exist")
        void fetchTasks_WhenEmpty_ShouldReturnEmptyList() {
            // Arrange
            when(taskRepository.findAll()).thenReturn(Collections.emptyList());
            when(taskMapper.map(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<TaskResponse> result = taskService.fetchTasks();

            // Assert
            assertThat(result).isEmpty();

            verify(taskRepository).findAll();
            verify(taskMapper).map(Collections.emptyList());
        }
    }

    @Nested
    @DisplayName("getTaskById")
    class GetTaskByIdTests {

        @Test
        @DisplayName("should return task response when task exists")
        void getTaskById_WhenExists_ShouldReturnTaskResponse() {
            // Arrange
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

            // Act
            TaskResponse result = taskService.getTaskById("1");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("Test Task");
            assertThat(result.status()).isEqualTo(TaskStatus.TODO);

            verify(taskRepository).findById(1L);
        }

        @Test
        @DisplayName("should throw exception when task not found")
        void getTaskById_WhenNotExists_ShouldThrowException() {
            // Arrange
            when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskService.getTaskById("999"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Task not found with id: 999");

            verify(taskRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("updateTask")
    class UpdateTaskTests {

        @Test
        @DisplayName("should save and return updated task")
        void updateTask_ShouldSaveAndReturnUpdatedTask() {
            // Arrange
            Task updatedTask = Task.builder()
                    .id(1L)
                    .title("Updated Task")
                    .description("Updated Description")
                    .status(TaskStatus.IN_PROGRESS)
                    .priority(TaskPriority.HIGH)
                    .dueDate(now.plusDays(14))
                    .build();

            when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

            // Act
            Task result = taskService.updateTask("1", sampleTaskUpdateRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Updated Task");
            assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(result.getPriority()).isEqualTo(TaskPriority.HIGH);

            verify(taskRepository).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("deleteTask")
    class DeleteTaskTests {

        @Test
        @DisplayName("should call repository deleteById")
        void deleteTask_ShouldCallRepositoryDelete() {
            // Act
            taskService.deleteTask("1");

            // Assert
            verify(taskRepository).deleteById(1L);
        }
    }
}
