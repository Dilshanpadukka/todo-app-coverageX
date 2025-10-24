package com.dilshan.coveragex.controller;

import com.dilshan.coveragex.dto.TaskStatusTypeResponseDTO;
import com.dilshan.coveragex.service.TaskStatusTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TaskStatusTypeController.class)
@ActiveProfiles("test")
class TaskStatusTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskStatusTypeService taskStatusTypeService;

    private List<TaskStatusTypeResponseDTO> taskStatusTypes;
    private TaskStatusTypeResponseDTO openStatusDTO;

    @BeforeEach
    void setUp() {
        // Setup task status type DTOs
        openStatusDTO = new TaskStatusTypeResponseDTO();
        openStatusDTO.setId(1L);
        openStatusDTO.setType("OPEN");

        TaskStatusTypeResponseDTO inProgressStatusDTO = new TaskStatusTypeResponseDTO();
        inProgressStatusDTO.setId(2L);
        inProgressStatusDTO.setType("IN_PROGRESS");

        TaskStatusTypeResponseDTO holdStatusDTO = new TaskStatusTypeResponseDTO();
        holdStatusDTO.setId(3L);
        holdStatusDTO.setType("HOLD");

        TaskStatusTypeResponseDTO doneStatusDTO = new TaskStatusTypeResponseDTO();
        doneStatusDTO.setId(4L);
        doneStatusDTO.setType("DONE");

        TaskStatusTypeResponseDTO closedStatusDTO = new TaskStatusTypeResponseDTO();
        closedStatusDTO.setId(5L);
        closedStatusDTO.setType("CLOSED");

        taskStatusTypes = Arrays.asList(openStatusDTO, inProgressStatusDTO, holdStatusDTO, doneStatusDTO, closedStatusDTO);
    }

    @Test
    void getAllTaskStatusTypes_Success() throws Exception {
        // Arrange
        when(taskStatusTypeService.getAllTaskStatusTypes()).thenReturn(taskStatusTypes);

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].type", is("OPEN")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].type", is("IN_PROGRESS")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].type", is("HOLD")))
                .andExpect(jsonPath("$[3].id", is(4)))
                .andExpect(jsonPath("$[3].type", is("DONE")))
                .andExpect(jsonPath("$[4].id", is(5)))
                .andExpect(jsonPath("$[4].type", is("CLOSED")));
    }

    @Test
    void getAllTaskStatusTypes_EmptyList() throws Exception {
        // Arrange
        when(taskStatusTypeService.getAllTaskStatusTypes()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getTaskStatusTypeById_Success() throws Exception {
        // Arrange
        when(taskStatusTypeService.getTaskStatusTypeById(1L)).thenReturn(openStatusDTO);

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.type", is("OPEN")));
    }

    @Test
    void getTaskStatusTypeById_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/task-status-types/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTaskStatusTypes_VerifyResponseStructure() throws Exception {
        // Arrange
        when(taskStatusTypeService.getAllTaskStatusTypes()).thenReturn(taskStatusTypes);

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[*].id", everyItem(isA(Number.class))))
                .andExpect(jsonPath("$[*].type", everyItem(isA(String.class))))
                .andExpect(jsonPath("$[*].type", containsInAnyOrder("OPEN", "IN_PROGRESS", "HOLD", "DONE", "CLOSED")));
    }

    @Test
    void getTaskStatusTypeById_VerifyResponseStructure() throws Exception {
        // Arrange
        when(taskStatusTypeService.getTaskStatusTypeById(1L)).thenReturn(openStatusDTO);

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.id", isA(Number.class)))
                .andExpect(jsonPath("$.type", isA(String.class)));
    }

    @Test
    void getAllTaskStatusTypes_CheckCorsHeaders() throws Exception {
        // Arrange
        when(taskStatusTypeService.getAllTaskStatusTypes()).thenReturn(taskStatusTypes);

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void getTaskStatusTypeById_CheckCorsHeaders() throws Exception {
        // Arrange
        when(taskStatusTypeService.getTaskStatusTypeById(1L)).thenReturn(openStatusDTO);

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types/1")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void getAllTaskStatusTypes_VerifyAllStatusTypes() throws Exception {
        // Arrange
        when(taskStatusTypeService.getAllTaskStatusTypes()).thenReturn(taskStatusTypes);

        // Act & Assert
        mockMvc.perform(get("/api/task-status-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.type == 'OPEN')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.type == 'IN_PROGRESS')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.type == 'HOLD')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.type == 'DONE')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.type == 'CLOSED')]", hasSize(1)));
    }
}
