package com.management.tasks.controllers;

import com.management.tasks.dto.request.TaskRequest;
import com.management.tasks.dto.request.TaskUpdateRequest;
import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.services.TaskServiceBusinessLogic;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

	private final TaskServiceBusinessLogic taskServiceBusinessLogic;

	/**
	 * Create a new task
	 *
	 * @return 201 CREATED with the created task
	 */
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

	/**
	 * Get all tasks
	 *
	 * @return 200 OK with list of tasks
	 */
	@GetMapping
	public ResponseEntity<List<TaskResponse>> getTasks() {
		var taskResponses = taskServiceBusinessLogic.fetchTasks();
		return new ResponseEntity<>(taskResponses, HttpStatus.OK);
	}

	/**
	 * Get a single task by ID
	 *
	 * @return 200 OK with the task, or 404 NOT FOUND if not exists
	 */
	@GetMapping("/{id}")
	public ResponseEntity<TaskResponse> getTaskById(@PathVariable("id") String id) {
		var taskResponse = taskServiceBusinessLogic.getTaskById(id);
		return new ResponseEntity<>(taskResponse, HttpStatus.OK);
	}

	/**
	 * Update an existing task
	 *
	 * @return 200 OK with the updated task
	 */
	@PutMapping(value = "/{id}", consumes = "application/json")
	public ResponseEntity<TaskResponse> updateTask(@PathVariable("id") String id,
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

	/**
	 * Delete a task by ID
	 *
	 * @return 204 NO CONTENT on success
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable("id") String id) {
		taskServiceBusinessLogic.deleteTask(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
