package com.management.tasks.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for natural language task creation via chat")
public record ChatRequest(String conversationId, String message) {
}
