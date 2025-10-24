package com.dilshan.coveragex.service;

import com.dilshan.coveragex.dto.*;
import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.Task;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.exception.TaskNotFoundException;
import com.dilshan.coveragex.repository.TaskRepository;
import com.dilshan.coveragex.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PriorityTypeService priorityTypeService;

    @Mock
    private TaskStatusTypeService taskStatusTypeService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task testTask;
    private TaskResponseDTO responseDTO;
    private TaskCreateRequestDTO createRequestDTO;
    private TaskUpdateRequestDTO updateRequestDTO;
    private PriorityType highPriority;
    private TaskStatusType openStatus;

    @BeforeEach
    void setUp() {
        // Setup test entities
        highPriority = new PriorityType(1L, "HIGH");
        openStatus = new TaskStatusType(1L, "OPEN");
        
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTaskTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setPriority(highPriority);
        testTask.setTaskStatus(openStatus);
        testTask.setCreateDate(LocalDateTime.now());
        testTask.setLastStatusChangeDate(LocalDateTime.now());

        // Setup DTOs
        responseDTO = new TaskResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTaskTitle("Test Task");
        responseDTO.setDescription("Test Description");

        createRequestDTO = new TaskCreateRequestDTO();
        createRequestDTO.setTaskTitle("New Task");
        createRequestDTO.setDescription("New Description");
        createRequestDTO.setPriorityId(1L);
        createRequestDTO.setTaskStatusId(1L);

        updateRequestDTO = new TaskUpdateRequestDTO();
        updateRequestDTO.setTaskTitle("Updated Task");
        updateRequestDTO.setDescription("Updated Description");
        updateRequestDTO.setPriorityId(1L);
        updateRequestDTO.setTaskStatusId(1L);
    }

    @Test
    void createTask_Success() {
        // Arrange
        when(priorityTypeService.getPriorityTypeEntityById(1L)).thenReturn(highPriority);
        when(taskStatusTypeService.getTaskStatusTypeEntityById(1L)).thenReturn(openStatus);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        TaskResponseDTO result = taskService.createTask(createRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getTaskTitle(), result.getTaskTitle());
        
        verify(priorityTypeService).getPriorityTypeEntityById(1L);
        verify(taskStatusTypeService).getTaskStatusTypeEntityById(1L);
        verify(taskRepository).save(any(Task.class));
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void getAllTasks_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        Page<TaskResponseDTO> result = taskService.getAllTasks(0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(responseDTO.getId(), result.getContent().get(0).getId());
        
        verify(taskRepository).findAll(any(Pageable.class));
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void getTaskById_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        TaskResponseDTO result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getTaskTitle(), result.getTaskTitle());
        
        verify(taskRepository).findById(1L);
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void getTaskById_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        
        verify(taskRepository).findById(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void updateTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(priorityTypeService.getPriorityTypeEntityById(1L)).thenReturn(highPriority);
        when(taskStatusTypeService.getTaskStatusTypeEntityById(1L)).thenReturn(openStatus);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        TaskResponseDTO result = taskService.updateTask(1L, updateRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void updateTask_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, updateRequestDTO));
        
        verify(taskRepository).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        TaskStatusType closedStatus = new TaskStatusType("CLOSED");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskStatusTypeService.getTaskStatusTypeEntityByType("CLOSED")).thenReturn(closedStatus);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository).findById(1L);
        verify(taskStatusTypeService).getTaskStatusTypeEntityByType("CLOSED");
        verify(taskRepository).save(testTask);
        assertEquals(closedStatus, testTask.getTaskStatus());
    }

    @Test
    void deleteTask_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
        
        verify(taskRepository).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTasksByStatus_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findByTaskStatusId(eq(1L), any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        Page<TaskResponseDTO> result = taskService.getTasksByStatus(1L, 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        verify(taskRepository).findByTaskStatusId(eq(1L), any(Pageable.class));
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void getTasksByPriority_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findByPriorityId(eq(1L), any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        Page<TaskResponseDTO> result = taskService.getTasksByPriority(1L, 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        verify(taskRepository).findByPriorityId(eq(1L), any(Pageable.class));
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void searchTasks_Success() {
        // Arrange
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.searchByTitleOrDescription(eq(searchTerm), any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        Page<TaskResponseDTO> result = taskService.searchTasks(searchTerm, 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        verify(taskRepository).searchByTitleOrDescription(eq(searchTerm), any(Pageable.class));
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void getTasksWithFilters_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findTasksWithFilters(eq(1L), eq(1L), eq("test"), any(Pageable.class))).thenReturn(taskPage);
        when(modelMapper.map(testTask, TaskResponseDTO.class)).thenReturn(responseDTO);

        // Act
        Page<TaskResponseDTO> result = taskService.getTasksWithFilters(1L, 1L, "test", 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        verify(taskRepository).findTasksWithFilters(eq(1L), eq(1L), eq("test"), any(Pageable.class));
        verify(modelMapper).map(testTask, TaskResponseDTO.class);
    }

    @Test
    void getTaskStatistics_Success() {
        // Arrange
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.countCompletedTasks()).thenReturn(5L);
        when(taskRepository.countActiveTasks()).thenReturn(5L);
        when(taskRepository.countTasksByStatus()).thenReturn(Arrays.asList(
            new Object[]{"OPEN", 3L},
            new Object[]{"IN_PROGRESS", 2L},
            new Object[]{"DONE", 5L}
        ));
        when(taskRepository.countTasksByPriority()).thenReturn(Arrays.asList(
            new Object[]{"HIGH", 4L},
            new Object[]{"MEDIUM", 3L},
            new Object[]{"LOW", 3L}
        ));

        // Act
        TaskStatisticsResponseDTO result = taskService.getTaskStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getTotalTasks());
        assertEquals(5L, result.getCompletedTasks());
        assertEquals(5L, result.getActiveTasks());
        assertNotNull(result.getTasksByStatus());
        assertNotNull(result.getTasksByPriority());
        
        verify(taskRepository).count();
        verify(taskRepository).countCompletedTasks();
        verify(taskRepository).countActiveTasks();
        verify(taskRepository).countTasksByStatus();
        verify(taskRepository).countTasksByPriority();
    }
}
