package com.dilshan.coveragex.controller;

import com.dilshan.coveragex.dto.TaskStatusTypeResponseDTO;
import com.dilshan.coveragex.service.TaskStatusTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-status-types")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class TaskStatusTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusTypeController.class);

    private final TaskStatusTypeService taskStatusTypeService;

    @Autowired
    public TaskStatusTypeController(TaskStatusTypeService taskStatusTypeService) {
        this.taskStatusTypeService = taskStatusTypeService;
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusTypeResponseDTO>> getAllTaskStatusTypes() {
        LOGGER.info("GET /api/task-status-types - Fetching all task status types");
        List<TaskStatusTypeResponseDTO> taskStatusTypes = taskStatusTypeService.getAllTaskStatusTypes();
        LOGGER.info("Successfully retrieved {} task status types", taskStatusTypes.size());
        return ResponseEntity.ok(taskStatusTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusTypeResponseDTO> getTaskStatusTypeById(
            @Parameter(description = "Task status type ID", required = true, example = "1")
            @PathVariable Long id) {
        LOGGER.info("GET /api/task-status-types/{} - Fetching task status type by ID", id);
        TaskStatusTypeResponseDTO taskStatusType = taskStatusTypeService.getTaskStatusTypeById(id);
        LOGGER.info("Successfully retrieved task status type: {}", taskStatusType.getType());
        return ResponseEntity.ok(taskStatusType);
    }
}
