package com.management.tasks.services;

import com.management.tasks.dto.request.TaskRequest;
import com.management.tasks.dto.request.TaskUpdateRequest;
import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import com.management.tasks.exceptions.TaskNotFoundException;
import com.management.tasks.mapper.TaskMapper;
import com.management.tasks.repository.ITaskRepositoryForAllTasks;
import com.management.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceBusinessLogic {

	private final TaskRepository taskRepository;
	private final ITaskRepositoryForAllTasks taskRepositoryForAllTasks;
	private final TaskMapper taskMapper;

	public Task createTask(TaskRequest taskRequest) {
		var task = Task.builder()
				.title(taskRequest.title())
				.description(taskRequest.description())
				.status(taskRequest.status())
				.priority(taskRequest.priority())
				.dueDate(taskRequest.dueDate())
				.build();
		return taskRepository.save(task);
	}

	public List<TaskResponse> fetchTasks() {
		var tasks = taskRepository.findAll();
		return taskMapper.map(tasks);
	}

	public TaskResponse getTaskById(String id) {
		var task = taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFoundException(id));
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getPriority(),
				task.getDueDate(),
				task.getCreatedAt(),
				task.getUpdatedAt());
	}

	public Task updateTask(String id, TaskUpdateRequest taskUpdateRequest) {
		var task = Task.builder()
				.id(id)
				.title(taskUpdateRequest.title())
				.description(taskUpdateRequest.description())
				.status(taskUpdateRequest.status())
				.priority(taskUpdateRequest.priority())
				.dueDate(taskUpdateRequest.dueDate())
				.build();
		return taskRepository.save(task);
	}

	public void deleteTask(String taskId) {

		taskRepository.deleteById(taskId);

	}

	public Page<TaskResponse> findAllTasksByFilters(TaskStatus taskStatus, TaskPriority taskPriority,
											int page, int size, String direction, String createdAt) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromOptionalString(direction).isEmpty()
						? Sort.DEFAULT_DIRECTION : Sort.Direction.fromOptionalString(direction).get(),
				createdAt));
		var tasks = taskRepositoryForAllTasks.findTasksUsingFilters(taskStatus, taskPriority, pageable);
		var taskResponse = tasks.map(task ->  new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getPriority(),
				task.getDueDate(),
				task.getCreatedAt(),
				task.getUpdatedAt()
		));
		log.debug("Tasks are {}", taskResponse);
		return taskResponse;
	}
}
