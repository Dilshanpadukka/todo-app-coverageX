package com.dilshan.coveragex.service.impl;

import com.dilshan.coveragex.dto.*;
import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.Task;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.exception.TaskNotFoundException;
import com.dilshan.coveragex.repository.TaskRepository;
import com.dilshan.coveragex.service.PriorityTypeService;
import com.dilshan.coveragex.service.TaskService;
import com.dilshan.coveragex.service.TaskStatusTypeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final PriorityTypeService priorityTypeService;
    private final TaskStatusTypeService taskStatusTypeService;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           PriorityTypeService priorityTypeService,
                           TaskStatusTypeService taskStatusTypeService,
                           ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.priorityTypeService = priorityTypeService;
        this.taskStatusTypeService = taskStatusTypeService;
        this.modelMapper = modelMapper;
    }

    @Override
    public TaskResponseDTO createTask(TaskCreateRequestDTO createRequestDTO) {
        LOGGER.info("Creating new task with title: {}", createRequestDTO.getTaskTitle());

        // Validate priority and status exist
        PriorityType priority = priorityTypeService.getPriorityTypeEntityById(createRequestDTO.getPriorityId());
        TaskStatusType taskStatus = taskStatusTypeService.getTaskStatusTypeEntityById(createRequestDTO.getTaskStatusId());

        // Create task entity
        Task task = new Task();
        task.setTaskTitle(createRequestDTO.getTaskTitle());
        task.setDescription(createRequestDTO.getDescription());
        task.setPriority(priority);
        task.setTaskStatus(taskStatus);

        // Save task
        Task savedTask = taskRepository.save(task);

        LOGGER.info("Successfully created task with ID: {}", savedTask.getId());

        return modelMapper.map(savedTask, TaskResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAllTasks(int page, int size, String sortBy, String sortDirection) {
        LOGGER.info("Fetching tasks - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, sortDirection);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Task> taskPage = taskRepository.findAll(pageable);

        LOGGER.info("Found {} tasks on page {} of {}",
                taskPage.getNumberOfElements(), taskPage.getNumber() + 1, taskPage.getTotalPages());

        return taskPage.map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        LOGGER.info("Fetching task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.forId(id));

        LOGGER.info("Found task: {}", task.getTaskTitle());

        return modelMapper.map(task, TaskResponseDTO.class);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskUpdateRequestDTO updateRequestDTO) {
        LOGGER.info("Updating task with ID: {}", id);

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.forId(id));

        // Update fields if provided
        if (updateRequestDTO.getTaskTitle() != null && !updateRequestDTO.getTaskTitle().trim().isEmpty()) {
            existingTask.setTaskTitle(updateRequestDTO.getTaskTitle());
        }

        if (updateRequestDTO.getDescription() != null) {
            existingTask.setDescription(updateRequestDTO.getDescription());
        }

        if (updateRequestDTO.getPriorityId() != null) {
            PriorityType priority = priorityTypeService.getPriorityTypeEntityById(updateRequestDTO.getPriorityId());
            existingTask.setPriority(priority);
        }

        if (updateRequestDTO.getTaskStatusId() != null) {
            TaskStatusType taskStatus = taskStatusTypeService.getTaskStatusTypeEntityById(updateRequestDTO.getTaskStatusId());
            existingTask.setTaskStatus(taskStatus);
        }

        Task updatedTask = taskRepository.save(existingTask);

        LOGGER.info("Successfully updated task with ID: {}", updatedTask.getId());

        return modelMapper.map(updatedTask, TaskResponseDTO.class);
    }

    @Override
    public void deleteTask(Long id) {
        LOGGER.info("Soft deleting task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.forId(id));

        // Find CLOSED status by type name to avoid hardcoding ID
        TaskStatusType closedStatus = taskStatusTypeService.getTaskStatusTypeEntityByType("CLOSED");
        task.setTaskStatus(closedStatus);

        taskRepository.save(task);

        LOGGER.info("Successfully soft deleted task with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByStatus(Long statusId, int page, int size, String sortBy, String sortDirection) {
        LOGGER.info("Fetching tasks by status ID: {}", statusId);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Task> taskPage = taskRepository.findByTaskStatusId(statusId, pageable);

        LOGGER.info("Found {} tasks with status ID: {}", taskPage.getTotalElements(), statusId);

        return taskPage.map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByPriority(Long priorityId, int page, int size, String sortBy, String sortDirection) {
        LOGGER.info("Fetching tasks by priority ID: {}", priorityId);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Task> taskPage = taskRepository.findByPriorityId(priorityId, pageable);

        LOGGER.info("Found {} tasks with priority ID: {}", taskPage.getTotalElements(), priorityId);

        return taskPage.map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> searchTasks(String searchTerm, int page, int size, String sortBy, String sortDirection) {
        LOGGER.info("Searching tasks with term: {}", searchTerm);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Task> taskPage = taskRepository.searchByTitleOrDescription(searchTerm, pageable);

        LOGGER.info("Found {} tasks matching search term: {}", taskPage.getTotalElements(), searchTerm);

        return taskPage.map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksWithFilters(Long statusId, Long priorityId, String searchTerm,
                                                     int page, int size, String sortBy, String sortDirection) {
        LOGGER.info("Fetching tasks with filters - statusId: {}, priorityId: {}, searchTerm: {}",
                statusId, priorityId, searchTerm);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Task> taskPage = taskRepository.findTasksWithFilters(statusId, priorityId, searchTerm, pageable);

        LOGGER.info("Found {} tasks with applied filters", taskPage.getTotalElements());

        return taskPage.map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskStatisticsResponseDTO getTaskStatistics() {
        LOGGER.info("Generating task statistics");

        Long totalTasks = taskRepository.count();
        Long completedTasks = taskRepository.countCompletedTasks();
        Long activeTasks = taskRepository.countActiveTasks();

        // Get tasks by status
        List<Object[]> statusCounts = taskRepository.countTasksByStatus();
        Map<String, Long> tasksByStatus = new HashMap<>();
        for (Object[] row : statusCounts) {
            tasksByStatus.put((String) row[0], (Long) row[1]);
        }

        // Get tasks by priority
        List<Object[]> priorityCounts = taskRepository.countTasksByPriority();
        Map<String, Long> tasksByPriority = new HashMap<>();
        for (Object[] row : priorityCounts) {
            tasksByPriority.put((String) row[0], (Long) row[1]);
        }

        TaskStatisticsResponseDTO statistics = new TaskStatisticsResponseDTO(
                totalTasks, tasksByStatus, tasksByPriority, completedTasks, activeTasks
        );

        LOGGER.info("Generated statistics - Total: {}, Completed: {}, Active: {}",
                totalTasks, completedTasks, activeTasks);

        return statistics;
    }
}
