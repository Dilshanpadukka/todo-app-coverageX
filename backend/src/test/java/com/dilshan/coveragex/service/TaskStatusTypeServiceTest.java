package com.dilshan.coveragex.service;

import com.dilshan.coveragex.dto.TaskStatusTypeResponseDTO;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.exception.TaskStatusTypeNotFoundException;
import com.dilshan.coveragex.repository.TaskStatusTypeRepository;
import com.dilshan.coveragex.service.impl.TaskStatusTypeServiceImpl;
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
class TaskStatusTypeServiceTest {

    @Mock
    private TaskStatusTypeRepository taskStatusTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskStatusTypeServiceImpl taskStatusTypeService;

    private TaskStatusType testTaskStatusType;
    private TaskStatusTypeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testTaskStatusType = new TaskStatusType(1L, "OPEN");
        
        responseDTO = new TaskStatusTypeResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setType("OPEN");
    }

    @Test
    void getAllTaskStatusTypes_Success() {
        // Arrange
        List<TaskStatusType> taskStatusTypes = Arrays.asList(testTaskStatusType);
        when(taskStatusTypeRepository.findAll()).thenReturn(taskStatusTypes);
        when(modelMapper.map(testTaskStatusType, TaskStatusTypeResponseDTO.class)).thenReturn(responseDTO);

        // Act
        List<TaskStatusTypeResponseDTO> result = taskStatusTypeService.getAllTaskStatusTypes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO.getId(), result.get(0).getId());
        assertEquals(responseDTO.getType(), result.get(0).getType());
        
        verify(taskStatusTypeRepository).findAll();
        verify(modelMapper).map(testTaskStatusType, TaskStatusTypeResponseDTO.class);
    }

    @Test
    void getAllTaskStatusTypes_EmptyList() {
        // Arrange
        when(taskStatusTypeRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<TaskStatusTypeResponseDTO> result = taskStatusTypeService.getAllTaskStatusTypes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(taskStatusTypeRepository).findAll();
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getTaskStatusTypeById_Success() {
        // Arrange
        when(taskStatusTypeRepository.findById(1L)).thenReturn(Optional.of(testTaskStatusType));
        when(modelMapper.map(testTaskStatusType, TaskStatusTypeResponseDTO.class)).thenReturn(responseDTO);

        // Act
        TaskStatusTypeResponseDTO result = taskStatusTypeService.getTaskStatusTypeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getType(), result.getType());
        
        verify(taskStatusTypeRepository).findById(1L);
        verify(modelMapper).map(testTaskStatusType, TaskStatusTypeResponseDTO.class);
    }

    @Test
    void getTaskStatusTypeById_NotFound() {
        // Arrange
        when(taskStatusTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskStatusTypeNotFoundException.class, 
                    () -> taskStatusTypeService.getTaskStatusTypeById(1L));
        
        verify(taskStatusTypeRepository).findById(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getTaskStatusTypeEntityById_Success() {
        // Arrange
        when(taskStatusTypeRepository.findById(1L)).thenReturn(Optional.of(testTaskStatusType));

        // Act
        TaskStatusType result = taskStatusTypeService.getTaskStatusTypeEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTaskStatusType.getId(), result.getId());
        assertEquals(testTaskStatusType.getType(), result.getType());
        
        verify(taskStatusTypeRepository).findById(1L);
    }

    @Test
    void getTaskStatusTypeEntityById_NotFound() {
        // Arrange
        when(taskStatusTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskStatusTypeNotFoundException.class, 
                    () -> taskStatusTypeService.getTaskStatusTypeEntityById(1L));
        
        verify(taskStatusTypeRepository).findById(1L);
    }

    @Test
    void getTaskStatusTypeByType_Success() {
        // Arrange
        when(taskStatusTypeRepository.findByType("OPEN")).thenReturn(Optional.of(testTaskStatusType));
        when(modelMapper.map(testTaskStatusType, TaskStatusTypeResponseDTO.class)).thenReturn(responseDTO);

        // Act
        TaskStatusTypeResponseDTO result = taskStatusTypeService.getTaskStatusTypeByType("OPEN");

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getType(), result.getType());
        
        verify(taskStatusTypeRepository).findByType("OPEN");
        verify(modelMapper).map(testTaskStatusType, TaskStatusTypeResponseDTO.class);
    }

    @Test
    void getTaskStatusTypeEntityByType_Success() {
        // Arrange
        when(taskStatusTypeRepository.findByType("OPEN")).thenReturn(Optional.of(testTaskStatusType));

        // Act
        TaskStatusType result = taskStatusTypeService.getTaskStatusTypeEntityByType("OPEN");

        // Assert
        assertNotNull(result);
        assertEquals(testTaskStatusType.getId(), result.getId());
        assertEquals(testTaskStatusType.getType(), result.getType());
        
        verify(taskStatusTypeRepository).findByType("OPEN");
    }

    @Test
    void existsById_Success() {
        // Arrange
        when(taskStatusTypeRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = taskStatusTypeService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(taskStatusTypeRepository).existsById(1L);
    }

    @Test
    void existsById_NotExists() {
        // Arrange
        when(taskStatusTypeRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean result = taskStatusTypeService.existsById(1L);

        // Assert
        assertFalse(result);
        verify(taskStatusTypeRepository).existsById(1L);
    }

    @Test
    void getAllTaskStatusTypes_MultipleTypes() {
        // Arrange
        TaskStatusType openStatus = new TaskStatusType(1L, "OPEN");
        TaskStatusType inProgressStatus = new TaskStatusType(2L, "IN_PROGRESS");
        TaskStatusType doneStatus = new TaskStatusType(3L, "DONE");
        TaskStatusType closedStatus = new TaskStatusType(4L, "CLOSED");
        
        List<TaskStatusType> statusTypes = Arrays.asList(openStatus, inProgressStatus, doneStatus, closedStatus);
        
        TaskStatusTypeResponseDTO openDTO = new TaskStatusTypeResponseDTO();
        openDTO.setId(1L);
        openDTO.setType("OPEN");
        
        TaskStatusTypeResponseDTO inProgressDTO = new TaskStatusTypeResponseDTO();
        inProgressDTO.setId(2L);
        inProgressDTO.setType("IN_PROGRESS");
        
        TaskStatusTypeResponseDTO doneDTO = new TaskStatusTypeResponseDTO();
        doneDTO.setId(3L);
        doneDTO.setType("DONE");
        
        TaskStatusTypeResponseDTO closedDTO = new TaskStatusTypeResponseDTO();
        closedDTO.setId(4L);
        closedDTO.setType("CLOSED");
        
        when(taskStatusTypeRepository.findAll()).thenReturn(statusTypes);
        when(modelMapper.map(openStatus, TaskStatusTypeResponseDTO.class)).thenReturn(openDTO);
        when(modelMapper.map(inProgressStatus, TaskStatusTypeResponseDTO.class)).thenReturn(inProgressDTO);
        when(modelMapper.map(doneStatus, TaskStatusTypeResponseDTO.class)).thenReturn(doneDTO);
        when(modelMapper.map(closedStatus, TaskStatusTypeResponseDTO.class)).thenReturn(closedDTO);

        // Act
        List<TaskStatusTypeResponseDTO> result = taskStatusTypeService.getAllTaskStatusTypes();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("OPEN", result.get(0).getType());
        assertEquals("IN_PROGRESS", result.get(1).getType());
        assertEquals("DONE", result.get(2).getType());
        assertEquals("CLOSED", result.get(3).getType());
        
        verify(taskStatusTypeRepository).findAll();
        verify(modelMapper, times(4)).map(any(TaskStatusType.class), eq(TaskStatusTypeResponseDTO.class));
    }

    @Test
    void getTaskStatusTypeByType_NotFound() {
        // Arrange
        when(taskStatusTypeRepository.findByType("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskStatusTypeNotFoundException.class, 
                    () -> taskStatusTypeService.getTaskStatusTypeByType("INVALID"));
        
        verify(taskStatusTypeRepository).findByType("INVALID");
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getTaskStatusTypeEntityByType_NotFound() {
        // Arrange
        when(taskStatusTypeRepository.findByType("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskStatusTypeNotFoundException.class, 
                    () -> taskStatusTypeService.getTaskStatusTypeEntityByType("INVALID"));
        
        verify(taskStatusTypeRepository).findByType("INVALID");
    }
}
