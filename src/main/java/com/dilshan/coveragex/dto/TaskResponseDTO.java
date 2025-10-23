package com.dilshan.coveragex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    private Long id;
    private String taskTitle;
    private String description;
    private LocalDateTime createDate;
    private LocalDateTime lastStatusChangeDate;
    private PriorityTypeResponseDTO priority;
    private TaskStatusTypeResponseDTO taskStatus;
}
