package com.dilshan.coveragex.controller;

import com.dilshan.coveragex.dto.TaskCreateRequestDTO;
import com.dilshan.coveragex.dto.TaskResponseDTO;
import com.dilshan.coveragex.dto.TaskStatisticsResponseDTO;
import com.dilshan.coveragex.dto.TaskUpdateRequestDTO;
import com.dilshan.coveragex.service.TaskService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = {"http://localhost:3000"})
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Parameter(description = "Task creation request", required = true)
            @Valid @RequestBody TaskCreateRequestDTO createRequestDTO) {
        LOGGER.info("POST /api/tasks - Creating new task: {}", createRequestDTO.getTaskTitle());
        TaskResponseDTO createdTask = taskService.createTask(createRequestDTO);
        LOGGER.info("Successfully created task with ID: {}", createdTask.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createDate")
            @RequestParam(defaultValue = "createDate") String sortBy,
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        LOGGER.info("GET /api/tasks - Fetching tasks with pagination: page={}, size={}, sortBy={}, direction={}", 
                   page, size, sortBy, sortDirection);
        Page<TaskResponseDTO> tasks = taskService.getAllTasks(page, size, sortBy, sortDirection);
        LOGGER.info("Successfully retrieved {} tasks", tasks.getNumberOfElements());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        LOGGER.info("GET /api/tasks/{} - Fetching task by ID", id);
        TaskResponseDTO task = taskService.getTaskById(id);
        LOGGER.info("Successfully retrieved task: {}", task.getTaskTitle());
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Task update request", required = true)
            @Valid @RequestBody TaskUpdateRequestDTO updateRequestDTO) {
        TaskResponseDTO updatedTask = taskService.updateTask(id, updateRequestDTO);
        LOGGER.info("Successfully updated task with ID: {}", updatedTask.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        taskService.deleteTask(id);
        LOGGER.info("Successfully soft deleted task with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByStatus(
            @Parameter(description = "Status ID", required = true, example = "1")
            @PathVariable Long statusId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createDate")
            @RequestParam(defaultValue = "createDate") String sortBy,
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        LOGGER.info("GET /api/tasks/status/{} - Fetching tasks by status", statusId);
        Page<TaskResponseDTO> tasks = taskService.getTasksByStatus(statusId, page, size, sortBy, sortDirection);
        LOGGER.info("Successfully retrieved {} tasks with status ID: {}", tasks.getNumberOfElements(), statusId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priorityId}")
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByPriority(
            @Parameter(description = "Priority ID", required = true, example = "1")
            @PathVariable Long priorityId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createDate")
            @RequestParam(defaultValue = "createDate") String sortBy,
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        LOGGER.info("GET /api/tasks/priority/{} - Fetching tasks by priority", priorityId);
        Page<TaskResponseDTO> tasks = taskService.getTasksByPriority(priorityId, page, size, sortBy, sortDirection);
        LOGGER.info("Successfully retrieved {} tasks with priority ID: {}", tasks.getNumberOfElements(), priorityId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponseDTO>> searchTasks(
            @Parameter(description = "Search term", required = true, example = "documentation")
            @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createDate")
            @RequestParam(defaultValue = "createDate") String sortBy,
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        LOGGER.info("GET /api/tasks/search - Searching tasks with term: {}", searchTerm);
        Page<TaskResponseDTO> tasks = taskService.searchTasks(searchTerm, page, size, sortBy, sortDirection);
        LOGGER.info("Successfully found {} tasks matching search term: {}", tasks.getNumberOfElements(), searchTerm);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<TaskResponseDTO>> getTasksWithFilters(
            @Parameter(description = "Status ID filter", example = "1")
            @RequestParam(required = false) Long statusId,
            @Parameter(description = "Priority ID filter", example = "2")
            @RequestParam(required = false) Long priorityId,
            @Parameter(description = "Search term filter", example = "documentation")
            @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "createDate")
            @RequestParam(defaultValue = "createDate") String sortBy,
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        LOGGER.info("GET /api/tasks/filter - Fetching tasks with filters: statusId={}, priorityId={}, searchTerm={}",
                   statusId, priorityId, searchTerm);
        Page<TaskResponseDTO> tasks = taskService.getTasksWithFilters(
                statusId, priorityId, searchTerm, page, size, sortBy, sortDirection);
        LOGGER.info("Successfully retrieved {} filtered tasks", tasks.getNumberOfElements());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/statistics")
    public ResponseEntity<TaskStatisticsResponseDTO> getTaskStatistics() {
        LOGGER.info("GET /api/tasks/statistics - Fetching task statistics");
        TaskStatisticsResponseDTO statistics = taskService.getTaskStatistics();
        LOGGER.info("Successfully retrieved task statistics - Total: {}, Completed: {}, Active: {}",
                   statistics.getTotalTasks(), statistics.getCompletedTasks(), statistics.getActiveTasks());
        return ResponseEntity.ok(statistics);
    }
}
