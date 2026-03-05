package com.management.tasks.controllers;

import com.management.tasks.dto.request.TaskRequest;
import com.management.tasks.dto.request.TaskUpdateRequest;
import com.management.tasks.dto.response.ErrorResponse;
import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import com.management.tasks.services.TaskServiceBusinessLogic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "CRUD operations for managing tasks")
public class TaskController {

	private final TaskServiceBusinessLogic taskServiceBusinessLogic;

	@Operation(summary = "Create a new task", description = "Creates a new task. Requires ROLE_USER.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized — authentication required"),
			@ApiResponse(responseCode = "403", description = "Forbidden — insufficient role")
	})
	@PreAuthorize("hasRole('USER')")
	@PostMapping(consumes = "application/json")
	public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
		var createdTask = taskServiceBusinessLogic.createTask(taskRequest);
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

	@Operation(summary = "Get all tasks with filters", description = "Retrieves a paginated list of tasks. Supports filtering by status and priority, "
			+ "sorting, and pagination. Requires ROLE_MANAGER.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized — authentication required"),
			@ApiResponse(responseCode = "403", description = "Forbidden — requires MANAGER role")
	})
	@PreAuthorize("hasRole('MANAGER')")
	@GetMapping
	public ResponseEntity<Page<TaskResponse>> getTasks(
			@Parameter(description = "Filter by task status") @RequestParam(required = false) TaskStatus taskStatus,
			@Parameter(description = "Filter by task priority") @RequestParam(required = false) TaskPriority taskPriority,
			@Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Field to sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
			@Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "desc") String direction) {
		var taskResponses = taskServiceBusinessLogic.findAllTasksByFilters(taskStatus, taskPriority, page, size,
				direction, sortBy);
		return new ResponseEntity<>(taskResponses, HttpStatus.OK);
	}

	@Operation(summary = "Get a task by ID", description = "Retrieves a single task by its MongoDB document ID. Requires ROLE_USER.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Task found", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
			@ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized — authentication required")
	})
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{id}")
	public ResponseEntity<TaskResponse> getTaskById(
			@Parameter(description = "Task ID", example = "507f1f77bcf86cd799439011") @PathVariable("id") String id) {
		var taskResponse = taskServiceBusinessLogic.getTaskById(id);
		return new ResponseEntity<>(taskResponse, HttpStatus.OK);
	}

	@Operation(summary = "Update a task", description = "Updates an existing task by ID. Requires ROLE_USER.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
			@ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized — authentication required")
	})
	@PreAuthorize("hasRole('USER')")
	@PutMapping(value = "/{id}", consumes = "application/json")
	public ResponseEntity<TaskResponse> updateTask(
			@Parameter(description = "Task ID", example = "507f1f77bcf86cd799439011") @PathVariable("id") String id,
			@Valid @RequestBody TaskUpdateRequest taskUpdateRequest) {
		var updatedTask = taskServiceBusinessLogic.updateTask(id, taskUpdateRequest);
		var response = new TaskResponse(
				updatedTask.getId(),
				updatedTask.getTitle(),
				updatedTask.getDescription(),
				updatedTask.getStatus(),
				updatedTask.getPriority(),
				updatedTask.getDueDate(),
				updatedTask.getCreatedAt(),
				updatedTask.getUpdatedAt());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Delete a task", description = "Deletes a task by ID. Requires ROLE_USER.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Task deleted successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized — authentication required")
	})
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(
			@Parameter(description = "Task ID", example = "507f1f77bcf86cd799439011") @PathVariable("id") String id) {
		taskServiceBusinessLogic.deleteTask(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
