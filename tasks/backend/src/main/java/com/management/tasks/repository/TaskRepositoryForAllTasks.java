package com.management.tasks.repository;

import com.management.tasks.entity.Task;
import com.management.tasks.entity.TaskPriority;
import com.management.tasks.entity.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TaskRepositoryForAllTasks implements ITaskRepositoryForAllTasks {

	private final MongoTemplate mongoTemplate;

	@Override
	public Page<Task> findTasksUsingFilters(TaskStatus status, TaskPriority priority, Pageable pageable) {
		Query query = new Query().with(pageable);
		if ( status != null ) {
			query.addCriteria(Criteria.where("status").is(status.name()));
		}
		if ( priority != null ) {
			query.addCriteria(Criteria.where("priority").is(priority.name()));
		}
		// Add this temporarily to see what's happening
		log.debug("MongoDB Query: {}", query);
		log.debug("Status filter: {}", status);
		List<Task> tasks = mongoTemplate.find(query, Task.class);
		long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Task.class);
		return new PageImpl<>(tasks, pageable, total);
	}
}
