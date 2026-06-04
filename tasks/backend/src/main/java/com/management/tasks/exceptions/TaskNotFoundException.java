package com.management.tasks.exceptions;

public class TaskNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TaskNotFoundException(String taskId) {
		super("Task is not found "+taskId);
	}
}
