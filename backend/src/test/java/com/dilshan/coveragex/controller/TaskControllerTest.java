package com.dilshan.coveragex.controller;

import com.dilshan.coveragex.dto.*;
import com.dilshan.coveragex.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskResponseDTO taskResponseDTO;
    private TaskCreateRequestDTO createRequestDTO;
    private TaskUpdateRequestDTO updateRequestDTO;
    private TaskStatisticsResponseDTO statisticsDTO;

    @BeforeEach
    void setUp() {
        // Setup response DTO
        taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(1L);
        taskResponseDTO.setTaskTitle("Test Task");
        taskResponseDTO.setDescription("Test Description");
        taskResponseDTO.setCreateDate(LocalDateTime.now());
        taskResponseDTO.setLastStatusChangeDate(LocalDateTime.now());

        // Setup priority and status DTOs
        PriorityTypeResponseDTO priorityDTO = new PriorityTypeResponseDTO();
        priorityDTO.setId(1L);
        priorityDTO.setType("HIGH");
        taskResponseDTO.setPriority(priorityDTO);

        TaskStatusTypeResponseDTO statusDTO = new TaskStatusTypeResponseDTO();
        statusDTO.setId(1L);
        statusDTO.setType("OPEN");
        taskResponseDTO.setTaskStatus(statusDTO);

        // Setup create request DTO
        createRequestDTO = new TaskCreateRequestDTO();
        createRequestDTO.setTaskTitle("New Task");
        createRequestDTO.setDescription("New Description");
        createRequestDTO.setPriorityId(1L);
        createRequestDTO.setTaskStatusId(1L);

        // Setup update request DTO
        updateRequestDTO = new TaskUpdateRequestDTO();
        updateRequestDTO.setTaskTitle("Updated Task");
        updateRequestDTO.setDescription("Updated Description");
        updateRequestDTO.setPriorityId(1L);
        updateRequestDTO.setTaskStatusId(1L);

        // Setup statistics DTO
        Map<String, Long> tasksByStatus = new HashMap<>();
        tasksByStatus.put("OPEN", 3L);
        tasksByStatus.put("IN_PROGRESS", 2L);
        tasksByStatus.put("DONE", 5L);

        Map<String, Long> tasksByPriority = new HashMap<>();
        tasksByPriority.put("HIGH", 4L);
        tasksByPriority.put("MEDIUM", 3L);
        tasksByPriority.put("LOW", 3L);

        statisticsDTO = new TaskStatisticsResponseDTO(10L, tasksByStatus, tasksByPriority, 5L, 5L);
    }

    @Test
    void createTask_Success() throws Exception {
        // Arrange
        when(taskService.createTask(any(TaskCreateRequestDTO.class))).thenReturn(taskResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.taskTitle", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.priority.type", is("HIGH")))
                .andExpect(jsonPath("$.taskStatus.type", is("OPEN")));
    }

    @Test
    void createTask_ValidationError() throws Exception {
        // Arrange - Create invalid request (missing required fields)
        TaskCreateRequestDTO invalidRequest = new TaskCreateRequestDTO();
        invalidRequest.setTaskTitle(""); // Invalid - blank title
        invalidRequest.setDescription("Valid description");
        // Missing priorityId and taskStatusId

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks_Success() throws Exception {
        // Arrange
        Page<TaskResponseDTO> taskPage = new PageImpl<>(Arrays.asList(taskResponseDTO));
        when(taskService.getAllTasks(anyInt(), anyInt(), anyString(), anyString())).thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createDate")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].taskTitle", is("Test Task")));
    }

    @Test
    void getAllTasks_DefaultParameters() throws Exception {
        // Arrange
        Page<TaskResponseDTO> taskPage = new PageImpl<>(Arrays.asList(taskResponseDTO));
        when(taskService.getAllTasks(0, 10, "createDate", "DESC")).thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getTaskById_Success() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenReturn(taskResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.taskTitle", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    void updateTask_Success() throws Exception {
        // Arrange
        when(taskService.updateTask(eq(1L), any(TaskUpdateRequestDTO.class))).thenReturn(taskResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.taskTitle", is("Test Task")));
    }

    @Test
    void deleteTask_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTasksByStatus_Success() throws Exception {
        // Arrange
        Page<TaskResponseDTO> taskPage = new PageImpl<>(Arrays.asList(taskResponseDTO));
        when(taskService.getTasksByStatus(eq(1L), anyInt(), anyInt(), anyString(), anyString())).thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/status/1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createDate")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].taskStatus.type", is("OPEN")));
    }

    @Test
    void getTasksByPriority_Success() throws Exception {
        // Arrange
        Page<TaskResponseDTO> taskPage = new PageImpl<>(Arrays.asList(taskResponseDTO));
        when(taskService.getTasksByPriority(eq(1L), anyInt(), anyInt(), anyString(), anyString())).thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/priority/1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createDate")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].priority.type", is("HIGH")));
    }

    @Test
    void searchTasks_Success() throws Exception {
        // Arrange
        Page<TaskResponseDTO> taskPage = new PageImpl<>(Arrays.asList(taskResponseDTO));
        when(taskService.searchTasks(eq("test"), anyInt(), anyInt(), anyString(), anyString())).thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/search")
                        .param("searchTerm", "test")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createDate")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getTasksWithFilters_Success() throws Exception {
        // Arrange
        Page<TaskResponseDTO> taskPage = new PageImpl<>(Arrays.asList(taskResponseDTO));
        when(taskService.getTasksWithFilters(eq(1L), eq(1L), eq("test"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(taskPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/filter")
                        .param("statusId", "1")
                        .param("priorityId", "1")
                        .param("searchTerm", "test")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createDate")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getTaskStatistics_Success() throws Exception {
        // Arrange
        when(taskService.getTaskStatistics()).thenReturn(statisticsDTO);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalTasks", is(10)))
                .andExpect(jsonPath("$.completedTasks", is(5)))
                .andExpect(jsonPath("$.activeTasks", is(5)))
                .andExpect(jsonPath("$.tasksByStatus.OPEN", is(3)))
                .andExpect(jsonPath("$.tasksByPriority.HIGH", is(4)));
    }
}
