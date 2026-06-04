package com.management.tasks.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard error response returned when an API error occurs")
public record ErrorResponse(
		@Schema(description = "HTTP status code", example = "404") Integer httpErrorCode,

		@Schema(description = "Error name", example = "Not Found") String errorName,

		@Schema(description = "Detailed error message", example = "Task is not found 507f1f77bcf86cd799439011") String errorMessage,

		@Schema(description = "Request path that caused the error", example = "/api/tasks/507f1f77bcf86cd799439011") String requestPath,

		@Schema(description = "Timestamp of the error") LocalDateTime timeStamp) {
}
