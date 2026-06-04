package com.management.tasks.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for natural language task creation")
public record MagicTaskRequest(@NotBlank(message = "Text cannot be blank")
							   @Schema(description = "Natural language description of the task",
									   example = "Remind me to call John about the project tomorrow morning.")
							   String chatText) {
}
