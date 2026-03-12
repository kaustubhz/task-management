package com.management.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "tasks")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

	@Id
	private String id;

	private String title;

	private String description;

	@Indexed(unique = true)
	private TaskStatus status;

	@Indexed(unique = true)
	private TaskPriority priority;

	private LocalDateTime dueDate;

	@CreatedDate
	@Indexed(unique = true)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

}
