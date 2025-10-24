package com.dilshan.coveragex.controller;

import com.dilshan.coveragex.dto.PriorityTypeResponseDTO;
import com.dilshan.coveragex.service.PriorityTypeService;
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

@WebMvcTest(PriorityTypeController.class)
@ActiveProfiles("test")
class PriorityTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriorityTypeService priorityTypeService;

    private List<PriorityTypeResponseDTO> priorityTypes;
    private PriorityTypeResponseDTO highPriorityDTO;

    @BeforeEach
    void setUp() {
        // Setup priority type DTOs
        highPriorityDTO = new PriorityTypeResponseDTO();
        highPriorityDTO.setId(1L);
        highPriorityDTO.setType("HIGH");

        PriorityTypeResponseDTO mediumPriorityDTO = new PriorityTypeResponseDTO();
        mediumPriorityDTO.setId(2L);
        mediumPriorityDTO.setType("MEDIUM");

        PriorityTypeResponseDTO lowPriorityDTO = new PriorityTypeResponseDTO();
        lowPriorityDTO.setId(3L);
        lowPriorityDTO.setType("LOW");

        priorityTypes = Arrays.asList(highPriorityDTO, mediumPriorityDTO, lowPriorityDTO);
    }

    @Test
    void getAllPriorityTypes_Success() throws Exception {
        // Arrange
        when(priorityTypeService.getAllPriorityTypes()).thenReturn(priorityTypes);

        // Act & Assert
        mockMvc.perform(get("/api/priority-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].type", is("HIGH")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].type", is("MEDIUM")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].type", is("LOW")));
    }

    @Test
    void getAllPriorityTypes_EmptyList() throws Exception {
        // Arrange
        when(priorityTypeService.getAllPriorityTypes()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/priority-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getPriorityTypeById_Success() throws Exception {
        // Arrange
        when(priorityTypeService.getPriorityTypeById(1L)).thenReturn(highPriorityDTO);

        // Act & Assert
        mockMvc.perform(get("/api/priority-types/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.type", is("HIGH")));
    }

    @Test
    void getPriorityTypeById_InvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/priority-types/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPriorityTypes_VerifyResponseStructure() throws Exception {
        // Arrange
        when(priorityTypeService.getAllPriorityTypes()).thenReturn(priorityTypes);

        // Act & Assert
        mockMvc.perform(get("/api/priority-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[*].id", everyItem(isA(Number.class))))
                .andExpect(jsonPath("$[*].type", everyItem(isA(String.class))))
                .andExpect(jsonPath("$[*].type", containsInAnyOrder("HIGH", "MEDIUM", "LOW")));
    }

    @Test
    void getPriorityTypeById_VerifyResponseStructure() throws Exception {
        // Arrange
        when(priorityTypeService.getPriorityTypeById(1L)).thenReturn(highPriorityDTO);

        // Act & Assert
        mockMvc.perform(get("/api/priority-types/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.id", isA(Number.class)))
                .andExpect(jsonPath("$.type", isA(String.class)));
    }

    @Test
    void getAllPriorityTypes_CheckCorsHeaders() throws Exception {
        // Arrange
        when(priorityTypeService.getAllPriorityTypes()).thenReturn(priorityTypes);

        // Act & Assert
        mockMvc.perform(get("/api/priority-types")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void getPriorityTypeById_CheckCorsHeaders() throws Exception {
        // Arrange
        when(priorityTypeService.getPriorityTypeById(1L)).thenReturn(highPriorityDTO);

        // Act & Assert
        mockMvc.perform(get("/api/priority-types/1")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}
