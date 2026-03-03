package com.management.tasks.repository;

import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.context.annotation.Import;
import com.management.tasks.config.TestSecurityConfig;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration"
})
@org.springframework.test.context.ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DisplayName("TaskRepository Integration Tests")
class TaskRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private Task sampleTask;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        sampleTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(now.plusDays(7))
                .build();
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("should persist task and generate ID")
        void save_ShouldPersistTask() {
            // Act
            Task savedTask = taskRepository.save(sampleTask);

            // Assert
            assertThat(savedTask.getId()).isNotNull();
            assertThat(savedTask.getTitle()).isEqualTo("Test Task");
            assertThat(savedTask.getDescription()).isEqualTo("Test Description");
            assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.TODO);
            assertThat(savedTask.getPriority()).isEqualTo(TaskPriority.MEDIUM);
            assertThat(savedTask.getCreatedAt()).isNotNull();
            assertThat(savedTask.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should persist task with all fields")
        void save_WithAllFields_ShouldPersistCorrectly() {
            // Arrange
            Task taskWithAllFields = Task.builder()
                    .title("Complete Task")
                    .description("Full description with details")
                    .status(TaskStatus.IN_PROGRESS)
                    .priority(TaskPriority.HIGH)
                    .dueDate(now.plusDays(30))
                    .build();

            // Act
            Task savedTask = taskRepository.save(taskWithAllFields);

            // Assert
            Task foundTask = mongoTemplate.findById(savedTask.getId(), Task.class);
            assertThat(foundTask).isNotNull();
            assertThat(foundTask.getTitle()).isEqualTo("Complete Task");
            assertThat(foundTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(foundTask.getPriority()).isEqualTo(TaskPriority.HIGH);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("should return task when exists")
        void findById_WhenExists_ShouldReturnTask() {
            // Arrange
            Task persistedTask = mongoTemplate.save(sampleTask);

            // Act
            Optional<Task> result = taskRepository.findById(persistedTask.getId());

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Test Task");
            assertThat(result.get().getStatus()).isEqualTo(TaskStatus.TODO);
        }

        @Test
        @DisplayName("should return empty when task not exists")
        void findById_WhenNotExists_ShouldReturnEmpty() {
            // Act
            Optional<Task> result = taskRepository.findById("non-existent-id");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("should return all tasks")
        void findAll_ShouldReturnAllTasks() {
            // Arrange
            Task task1 = Task.builder()
                    .title("Task 1")
                    .description("Description 1")
                    .status(TaskStatus.TODO)
                    .priority(TaskPriority.LOW)
                    .build();

            Task task2 = Task.builder()
                    .title("Task 2")
                    .description("Description 2")
                    .status(TaskStatus.COMPLETED)
                    .priority(TaskPriority.HIGH)
                    .build();

            mongoTemplate.save(task1);
            mongoTemplate.save(task2);

            // Act
            List<Task> result = taskRepository.findAll();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Task::getTitle)
                    .containsExactlyInAnyOrder("Task 1", "Task 2");
        }

        @Test
        @DisplayName("should return empty list when no tasks")
        void findAll_WhenEmpty_ShouldReturnEmptyList() {
            // Act
            List<Task> result = taskRepository.findAll();

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTests {

        @Test
        @DisplayName("should remove task from database")
        void deleteById_ShouldRemoveTask() {
            // Arrange
            Task persistedTask = mongoTemplate.save(sampleTask);
            String taskId = persistedTask.getId();

            // Act
            taskRepository.deleteById(taskId);

            // Assert
            Task foundTask = mongoTemplate.findById(taskId, Task.class);
            assertThat(foundTask).isNull();
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("should update existing task")
        void save_ExistingTask_ShouldUpdate() {
            // Arrange
            Task persistedTask = mongoTemplate.save(sampleTask);

            // Act
            persistedTask.setTitle("Updated Title");
            persistedTask.setStatus(TaskStatus.COMPLETED);
            Task updatedTask = taskRepository.save(persistedTask);

            // Assert
            Task foundTask = mongoTemplate.findById(persistedTask.getId(), Task.class);
            assertThat(foundTask.getTitle()).isEqualTo("Updated Title");
            assertThat(foundTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        }
    }
}
