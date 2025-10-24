package com.dilshan.coveragex.service;

import com.dilshan.coveragex.dto.*;
import org.springframework.data.domain.Page;


public interface TaskService {

    TaskResponseDTO createTask(TaskCreateRequestDTO createRequestDTO);

    Page<TaskResponseDTO> getAllTasks(int page, int size, String sortBy, String sortDirection);

    TaskResponseDTO getTaskById(Long id);

    TaskResponseDTO updateTask(Long id, TaskUpdateRequestDTO updateRequestDTO);

    void deleteTask(Long id);

    Page<TaskResponseDTO> getTasksByStatus(Long statusId, int page, int size, String sortBy, String sortDirection);

    Page<TaskResponseDTO> getTasksByPriority(Long priorityId, int page, int size, String sortBy, String sortDirection);

    Page<TaskResponseDTO> searchTasks(String searchTerm, int page, int size, String sortBy, String sortDirection);

    Page<TaskResponseDTO> getTasksWithFilters(Long statusId, Long priorityId, String searchTerm,
                                            int page, int size, String sortBy, String sortDirection);

    TaskStatisticsResponseDTO getTaskStatistics();
}

