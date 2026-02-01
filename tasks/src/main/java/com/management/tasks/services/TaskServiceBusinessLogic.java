package com.management.tasks.services;

import com.management.tasks.dto.request.TaskRequest;
import com.management.tasks.dto.request.TaskUpdateRequest;
import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.Task;
import com.management.tasks.mapper.TaskMapper;
import com.management.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceBusinessLogic {

	private final TaskRepository taskRepository;
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
		var task = taskRepository.findById(Long.parseLong(id))
				.orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
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
				.id(Long.parseLong(id))
				.title(taskUpdateRequest.title())
				.description(taskUpdateRequest.description())
				.status(taskUpdateRequest.status())
				.priority(taskUpdateRequest.priority())
				.dueDate(taskUpdateRequest.dueDate())
				.build();
		return taskRepository.save(task);
	}

	public void deleteTask(String taskId) {

		taskRepository.deleteById(Long.valueOf(taskId));

	}
}
