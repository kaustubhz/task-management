package com.management.tasks.entity;

public enum TaskPriority {
	LOW(1,"Low priority for task"),
	MEDIUM(1,"Medium priority for task"),
	HIGH(1,"High priority for task");
	private final int priority;
	private final String description;

	TaskPriority(int priority, String description) {
		this.priority = priority;
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public String getDescription() {
		return description;
	}
}
