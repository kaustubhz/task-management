package com.management.tasks.repository;

import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

	Page<Task> findByStatus(TaskStatus status, Pageable pageable);

	Page<Task> findByPriority(TaskPriority taskPriority, Pageable pageable);

	Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority taskPriority, Pageable pageable);

}
