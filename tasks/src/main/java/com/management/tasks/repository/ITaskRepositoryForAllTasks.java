package com.management.tasks.repository;

import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITaskRepositoryForAllTasks {
	Page<Task> findTasksUsingFilters(TaskStatus status, TaskPriority priority, Pageable pageable);
}
