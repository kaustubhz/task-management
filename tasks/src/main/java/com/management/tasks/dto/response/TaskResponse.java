package com.management.tasks.dto.response;

import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "Task response returned by the API")
public record TaskResponse(
		@Schema(description = "Unique task ID (MongoDB ObjectId)", example = "507f1f77bcf86cd799439011") String id,

		@Schema(description = "Title of the task", example = "Implement login feature") String title,

		@Schema(description = "Detailed description", example = "Add Spring Security login") String description,

		@Schema(description = "Current status", example = "TODO") TaskStatus status,

		@Schema(description = "Priority level", example = "HIGH") TaskPriority priority,

		@Schema(description = "Due date for the task") LocalDateTime dueDate,

		@Schema(description = "When the task was created") LocalDateTime createdAt,

		@Schema(description = "When the task was last updated") LocalDateTime updatedAt)
		implements Serializable {
}
