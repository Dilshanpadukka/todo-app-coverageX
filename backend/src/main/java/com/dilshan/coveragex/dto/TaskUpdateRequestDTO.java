package com.dilshan.coveragex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequestDTO {
    @Size(min = 1, max = 255, message = "Task title must be between 1 and 255 characters")
    private String taskTitle;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Positive(message = "Priority ID must be positive")
    private Long priorityId;

    @Positive(message = "Task status ID must be positive")
    private Long taskStatusId;
}
