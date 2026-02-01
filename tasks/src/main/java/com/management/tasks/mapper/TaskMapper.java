package com.management.tasks.mapper;

import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.Task;

import java.util.List;

public interface TaskMapper {
	List<TaskResponse> map(List<Task> tasks);
}
