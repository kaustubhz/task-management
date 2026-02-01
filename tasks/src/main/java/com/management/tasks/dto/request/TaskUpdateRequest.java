package com.management.tasks.dto.request;

import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;

import java.time.LocalDateTime;

public record TaskUpdateRequest(
		String title,
		String description,
		TaskStatus status,
		TaskPriority priority,
		LocalDateTime dueDate
) {
}
