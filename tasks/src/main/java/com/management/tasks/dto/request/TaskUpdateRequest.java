package com.management.tasks.dto.request;

import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Request body for updating an existing task. All fields are optional.")
public record TaskUpdateRequest(
		@Schema(description = "Updated title", example = "Implement OAuth2 login") String title,

		@Schema(description = "Updated description", example = "Integrate Keycloak as OAuth2 provider") String description,

		@Schema(description = "Updated status", example = "IN_PROGRESS") TaskStatus status,

		@Schema(description = "Updated priority", example = "MEDIUM") TaskPriority priority,

		@Schema(description = "Updated due date", example = "2026-04-01T18:00:00") OffsetDateTime dueDate) {
}
