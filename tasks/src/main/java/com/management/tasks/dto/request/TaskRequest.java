package com.management.tasks.dto.request;

import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Schema(description = "Request body for creating a new task")
public record TaskRequest(
		@Schema(description = "Title of the task", example = "Implement login feature") @NotBlank(message = "Title is required") String title,

		@Schema(description = "Detailed description of the task", example = "Add username/password login using Spring Security") String description,

		@Schema(description = "Current status of the task", example = "TODO") @NotNull(message = "Status is required") TaskStatus status,

		@Schema(description = "Priority level of the task", example = "HIGH") @NotNull(message = "Priority is required") TaskPriority priority,

		@Schema(description = "Due date for the task", example = "2026-03-15T10:00:00") OffsetDateTime dueDate) {
}
