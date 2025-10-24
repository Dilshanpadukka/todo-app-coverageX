package com.dilshan.coveragex.service;

import com.dilshan.coveragex.dto.PriorityTypeResponseDTO;
import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.exception.PriorityTypeNotFoundException;
import com.dilshan.coveragex.repository.PriorityTypeRepository;
import com.dilshan.coveragex.service.impl.PriorityTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriorityTypeServiceTest {

    @Mock
    private PriorityTypeRepository priorityTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PriorityTypeServiceImpl priorityTypeService;

    private PriorityType testPriorityType;
    private PriorityTypeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testPriorityType = new PriorityType(1L, "HIGH");
        
        responseDTO = new PriorityTypeResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType("HIGH");
    }

    @Test
    void getAllPriorityTypes_Success() {
        // Arrange
        List<PriorityType> priorityTypes = Arrays.asList(testPriorityType);
        when(priorityTypeRepository.findAll()).thenReturn(priorityTypes);
        when(modelMapper.map(testPriorityType, PriorityTypeResponseDTO.class)).thenReturn(responseDTO);

        // Act
        List<PriorityTypeResponseDTO> result = priorityTypeService.getAllPriorityTypes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO.getId(), result.get(0).getId());
        assertEquals(responseDTO.getType(), result.get(0).getType());
        
        verify(priorityTypeRepository).findAll();
        verify(modelMapper).map(testPriorityType, PriorityTypeResponseDTO.class);
    }

    @Test
    void getAllPriorityTypes_EmptyList() {
        // Arrange
        when(priorityTypeRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<PriorityTypeResponseDTO> result = priorityTypeService.getAllPriorityTypes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(priorityTypeRepository).findAll();
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getPriorityTypeById_Success() {
        // Arrange
        when(priorityTypeRepository.findById(1L)).thenReturn(Optional.of(testPriorityType));
        when(modelMapper.map(testPriorityType, PriorityTypeResponseDTO.class)).thenReturn(responseDTO);

        // Act
        PriorityTypeResponseDTO result = priorityTypeService.getPriorityTypeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getType(), result.getType());
        
        verify(priorityTypeRepository).findById(1L);
        verify(modelMapper).map(testPriorityType, PriorityTypeResponseDTO.class);
    }

    @Test
    void getPriorityTypeById_NotFound() {
        // Arrange
        when(priorityTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PriorityTypeNotFoundException.class, 
                    () -> priorityTypeService.getPriorityTypeById(1L));
        
        verify(priorityTypeRepository).findById(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getPriorityTypeEntityById_Success() {
        // Arrange
        when(priorityTypeRepository.findById(1L)).thenReturn(Optional.of(testPriorityType));

        // Act
        PriorityType result = priorityTypeService.getPriorityTypeEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testPriorityType.getId(), result.getId());
        assertEquals(testPriorityType.getType(), result.getType());
        
        verify(priorityTypeRepository).findById(1L);
    }

    @Test
    void getPriorityTypeEntityById_NotFound() {
        // Arrange
        when(priorityTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PriorityTypeNotFoundException.class, 
                    () -> priorityTypeService.getPriorityTypeEntityById(1L));
        
        verify(priorityTypeRepository).findById(1L);
    }

    @Test
    void getPriorityTypeByType_Success() {
        // Arrange
        when(priorityTypeRepository.findByType("HIGH")).thenReturn(Optional.of(testPriorityType));
        when(modelMapper.map(testPriorityType, PriorityTypeResponseDTO.class)).thenReturn(responseDTO);

        // Act
        PriorityTypeResponseDTO result = priorityTypeService.getPriorityTypeByType("HIGH");

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getType(), result.getType());
        
        verify(priorityTypeRepository).findByType("HIGH");
        verify(modelMapper).map(testPriorityType, PriorityTypeResponseDTO.class);
    }

    @Test
    void getPriorityTypeByType_NotFound() {
        // Arrange
        when(priorityTypeRepository.findByType("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PriorityTypeNotFoundException.class, 
                    () -> priorityTypeService.getPriorityTypeByType("INVALID"));
        
        verify(priorityTypeRepository).findByType("INVALID");
        verifyNoInteractions(modelMapper);
    }

    @Test
    void existsById_Success() {
        // Arrange
        when(priorityTypeRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = priorityTypeService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(priorityTypeRepository).existsById(1L);
    }

    @Test
    void existsById_NotExists() {
        // Arrange
        when(priorityTypeRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean result = priorityTypeService.existsById(1L);

        // Assert
        assertFalse(result);
        verify(priorityTypeRepository).existsById(1L);
    }

    @Test
    void getAllPriorityTypes_MultipleTypes() {
        // Arrange
        PriorityType highPriority = new PriorityType(1L, "HIGH");
        PriorityType mediumPriority = new PriorityType(2L, "MEDIUM");
        PriorityType lowPriority = new PriorityType(3L, "LOW");
        
        List<PriorityType> priorityTypes = Arrays.asList(highPriority, mediumPriority, lowPriority);
        
        PriorityTypeResponseDTO highDTO = new PriorityTypeResponseDTO();
        highDTO.setId(1L);
        highDTO.setType("HIGH");
        
        PriorityTypeResponseDTO mediumDTO = new PriorityTypeResponseDTO();
        mediumDTO.setId(2L);
        mediumDTO.setType("MEDIUM");
        
        PriorityTypeResponseDTO lowDTO = new PriorityTypeResponseDTO();
        lowDTO.setId(3L);
        lowDTO.setType("LOW");
        
        when(priorityTypeRepository.findAll()).thenReturn(priorityTypes);
        when(modelMapper.map(highPriority, PriorityTypeResponseDTO.class)).thenReturn(highDTO);
        when(modelMapper.map(mediumPriority, PriorityTypeResponseDTO.class)).thenReturn(mediumDTO);
        when(modelMapper.map(lowPriority, PriorityTypeResponseDTO.class)).thenReturn(lowDTO);

        // Act
        List<PriorityTypeResponseDTO> result = priorityTypeService.getAllPriorityTypes();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("HIGH", result.get(0).getType());
        assertEquals("MEDIUM", result.get(1).getType());
        assertEquals("LOW", result.get(2).getType());
        
        verify(priorityTypeRepository).findAll();
        verify(modelMapper, times(3)).map(any(PriorityType.class), eq(PriorityTypeResponseDTO.class));
    }
}
