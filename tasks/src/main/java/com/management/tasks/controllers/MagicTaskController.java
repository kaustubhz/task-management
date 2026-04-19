package com.management.tasks.controllers;

import com.management.tasks.dto.request.MagicTaskRequest;
import com.management.tasks.dto.request.TaskRequest;
import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.services.AIService;
import com.management.tasks.services.TaskServiceBusinessLogic;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@ConditionalOnProperty(name = "features.ai-enabled", havingValue = "true")
public class MagicTaskController {

	private final AIService aiService;
	private final TaskServiceBusinessLogic taskServiceBusinessLogic;

	@PostMapping("/magic")
	public ResponseEntity<TaskResponse> createMagicTask(@RequestBody @Valid MagicTaskRequest magicTaskRequest) {

		var structuredRequest = aiService.extractTaskFromText(magicTaskRequest.chatText());
		var createdTask = taskServiceBusinessLogic.createTask(structuredRequest);
		var response = new TaskResponse(
				createdTask.getId(),
				createdTask.getTitle(),
				createdTask.getDescription(),
				createdTask.getStatus(),
				createdTask.getPriority(),
				createdTask.getDueDate(),
				createdTask.getCreatedAt(),
				createdTask.getUpdatedAt());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
