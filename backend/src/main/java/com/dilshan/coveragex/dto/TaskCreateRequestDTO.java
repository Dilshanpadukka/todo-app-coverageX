package com.dilshan.coveragex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequestDTO {
    @NotBlank(message = "Task title cannot be blank")
    @Size(min = 1, max = 255, message = "Task title must be between 1 and 255 characters")
    private String taskTitle;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Priority ID is required")
    @Positive(message = "Priority ID must be positive")
    private Long priorityId;

    @NotNull(message = "Task status ID is required")
    @Positive(message = "Task status ID must be positive")
    private Long taskStatusId;
}
