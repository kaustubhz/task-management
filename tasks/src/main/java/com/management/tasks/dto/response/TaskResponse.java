package com.management.tasks.dto.response;

import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
		String id,
		String title,
		String description,
		TaskStatus status,
		TaskPriority priority,
		LocalDateTime dueDate,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {
}
