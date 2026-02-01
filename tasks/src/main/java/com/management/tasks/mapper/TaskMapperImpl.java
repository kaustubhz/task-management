package com.management.tasks.mapper;

import com.management.tasks.dto.response.TaskResponse;
import com.management.tasks.entity.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public List<TaskResponse> map(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }
        return tasks.stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }

    private TaskResponse toTaskResponse(Task task) {
        if (task == null) {
            return null;
        }
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
}
