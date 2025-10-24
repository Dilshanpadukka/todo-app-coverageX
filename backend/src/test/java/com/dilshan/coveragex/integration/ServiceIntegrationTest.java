package com.dilshan.coveragex.integration;

import com.dilshan.coveragex.dto.*;
import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.repository.PriorityTypeRepository;
import com.dilshan.coveragex.repository.TaskRepository;
import com.dilshan.coveragex.repository.TaskStatusTypeRepository;
import com.dilshan.coveragex.service.PriorityTypeService;
import com.dilshan.coveragex.service.TaskService;
import com.dilshan.coveragex.service.TaskStatusTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private PriorityTypeService priorityTypeService;

    @Autowired
    private TaskStatusTypeService taskStatusTypeService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PriorityTypeRepository priorityTypeRepository;

    @Autowired
    private TaskStatusTypeRepository taskStatusTypeRepository;

    private PriorityType highPriority;
    private PriorityType mediumPriority;
    private TaskStatusType openStatus;
    private TaskStatusType inProgressStatus;
    private TaskStatusType doneStatus;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        taskRepository.deleteAll();
        priorityTypeRepository.deleteAll();
        taskStatusTypeRepository.deleteAll();

        // Setup test data - check if exists first to avoid duplicates
        highPriority = priorityTypeRepository.findByType("HIGH")
                .orElseGet(() -> priorityTypeRepository.save(new PriorityType("HIGH")));
        mediumPriority = priorityTypeRepository.findByType("MEDIUM")
                .orElseGet(() -> priorityTypeRepository.save(new PriorityType("MEDIUM")));
        PriorityType lowPriority = priorityTypeRepository.findByType("LOW")
                .orElseGet(() -> priorityTypeRepository.save(new PriorityType("LOW")));

        openStatus = taskStatusTypeRepository.findByType("OPEN")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("OPEN")));
        inProgressStatus = taskStatusTypeRepository.findByType("IN_PROGRESS")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("IN_PROGRESS")));
        doneStatus = taskStatusTypeRepository.findByType("DONE")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("DONE")));
        TaskStatusType closedStatus = taskStatusTypeRepository.findByType("CLOSED")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("CLOSED")));
    }

    @Test
    void taskService_createTask_Success() {
        // Arrange
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Integration Test Task");
        createRequest.setDescription("This is a test task for service integration testing");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        // Act
        TaskResponseDTO result = taskService.createTask(createRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Integration Test Task", result.getTaskTitle());
        assertEquals("This is a test task for service integration testing", result.getDescription());
        assertNotNull(result.getPriority());
        assertEquals("HIGH", result.getPriority().getType());
        assertNotNull(result.getTaskStatus());
        assertEquals("OPEN", result.getTaskStatus().getType());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getLastStatusChangeDate());
    }

    @Test
    void taskService_getAllTasks_Success() {
        // Arrange - Create a test task
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Test Task");
        createRequest.setDescription("Test Description");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());
        
        TaskResponseDTO createdTask = taskService.createTask(createRequest);

        // Act
        Page<TaskResponseDTO> result = taskService.getAllTasks(0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(createdTask.getId(), result.getContent().get(0).getId());
    }

    @Test
    void taskService_getTaskById_Success() {
        // Arrange - Create a test task
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Test Task");
        createRequest.setDescription("Test Description");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());
        
        TaskResponseDTO createdTask = taskService.createTask(createRequest);

        // Act
        TaskResponseDTO result = taskService.getTaskById(createdTask.getId());

        // Assert
        assertNotNull(result);
        assertEquals(createdTask.getId(), result.getId());
        assertEquals("Test Task", result.getTaskTitle());
        assertEquals("Test Description", result.getDescription());
    }

    @Test
    void taskService_updateTask_Success() {
        // Arrange - Create a test task
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Original Task");
        createRequest.setDescription("Original Description");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());
        
        TaskResponseDTO createdTask = taskService.createTask(createRequest);

        TaskUpdateRequestDTO updateRequest = new TaskUpdateRequestDTO();
        updateRequest.setTaskTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriorityId(mediumPriority.getId());
        updateRequest.setTaskStatusId(inProgressStatus.getId());

        // Act
        TaskResponseDTO result = taskService.updateTask(createdTask.getId(), updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(createdTask.getId(), result.getId());
        assertEquals("Updated Task", result.getTaskTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("MEDIUM", result.getPriority().getType());
        assertEquals("IN_PROGRESS", result.getTaskStatus().getType());
    }

    @Test
    void taskService_deleteTask_Success() {
        // Arrange - Create CLOSED status for soft delete, check if exists first
        TaskStatusType closedStatus = taskStatusTypeRepository.findByType("CLOSED")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("CLOSED")));

        // Create a test task
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Task to Delete");
        createRequest.setDescription("This task will be deleted");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        TaskResponseDTO createdTask = taskService.createTask(createRequest);

        // Act
        taskService.deleteTask(createdTask.getId());

        // Assert - Task should still exist but with CLOSED status (soft delete)
        TaskResponseDTO deletedTask = taskService.getTaskById(createdTask.getId());
        assertNotNull(deletedTask);
        assertEquals("CLOSED", deletedTask.getTaskStatus().getType());
    }

    @Test
    void taskService_getTasksByStatus_Success() {
        // Arrange - Create tasks with different statuses
        TaskCreateRequestDTO openTask = new TaskCreateRequestDTO();
        openTask.setTaskTitle("Open Task");
        openTask.setDescription("Task with open status");
        openTask.setPriorityId(highPriority.getId());
        openTask.setTaskStatusId(openStatus.getId());

        TaskCreateRequestDTO inProgressTask = new TaskCreateRequestDTO();
        inProgressTask.setTaskTitle("In Progress Task");
        inProgressTask.setDescription("Task with in progress status");
        inProgressTask.setPriorityId(highPriority.getId());
        inProgressTask.setTaskStatusId(inProgressStatus.getId());

        taskService.createTask(openTask);
        taskService.createTask(inProgressTask);

        // Act
        Page<TaskResponseDTO> result = taskService.getTasksByStatus(openStatus.getId(), 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Open Task", result.getContent().get(0).getTaskTitle());
        assertEquals("OPEN", result.getContent().get(0).getTaskStatus().getType());
    }

    @Test
    void taskService_getTasksByPriority_Success() {
        // Arrange - Create tasks with different priorities
        TaskCreateRequestDTO highPriorityTask = new TaskCreateRequestDTO();
        highPriorityTask.setTaskTitle("High Priority Task");
        highPriorityTask.setDescription("Task with high priority");
        highPriorityTask.setPriorityId(highPriority.getId());
        highPriorityTask.setTaskStatusId(openStatus.getId());

        TaskCreateRequestDTO mediumPriorityTask = new TaskCreateRequestDTO();
        mediumPriorityTask.setTaskTitle("Medium Priority Task");
        mediumPriorityTask.setDescription("Task with medium priority");
        mediumPriorityTask.setPriorityId(mediumPriority.getId());
        mediumPriorityTask.setTaskStatusId(openStatus.getId());

        taskService.createTask(highPriorityTask);
        taskService.createTask(mediumPriorityTask);

        // Act
        Page<TaskResponseDTO> result = taskService.getTasksByPriority(highPriority.getId(), 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("High Priority Task", result.getContent().get(0).getTaskTitle());
        assertEquals("HIGH", result.getContent().get(0).getPriority().getType());
    }

    @Test
    void taskService_searchTasks_Success() {
        // Arrange - Create test tasks
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Searchable Task");
        createRequest.setDescription("This task contains searchable content");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());
        
        taskService.createTask(createRequest);

        // Act
        Page<TaskResponseDTO> result = taskService.searchTasks("Searchable", 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getTaskTitle().contains("Searchable"));
    }

    @Test
    void taskService_getTasksWithFilters_Success() {
        // Arrange - Create test tasks
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Filtered Task");
        createRequest.setDescription("This task will be filtered");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());
        
        taskService.createTask(createRequest);

        // Act
        Page<TaskResponseDTO> result = taskService.getTasksWithFilters(
                openStatus.getId(), highPriority.getId(), "Filtered", 0, 10, "createDate", "DESC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Filtered Task", result.getContent().get(0).getTaskTitle());
    }

    @Test
    void taskService_getTaskStatistics_Success() {
        // Arrange - Create tasks with different statuses and priorities
        TaskCreateRequestDTO openHighTask = new TaskCreateRequestDTO();
        openHighTask.setTaskTitle("Open High Task");
        openHighTask.setDescription("Open task with high priority");
        openHighTask.setPriorityId(highPriority.getId());
        openHighTask.setTaskStatusId(openStatus.getId());

        TaskCreateRequestDTO doneHighTask = new TaskCreateRequestDTO();
        doneHighTask.setTaskTitle("Done High Task");
        doneHighTask.setDescription("Done task with high priority");
        doneHighTask.setPriorityId(highPriority.getId());
        doneHighTask.setTaskStatusId(doneStatus.getId());

        taskService.createTask(openHighTask);
        taskService.createTask(doneHighTask);

        // Act
        TaskStatisticsResponseDTO result = taskService.getTaskStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalTasks());
        assertEquals(1L, result.getCompletedTasks());
        assertEquals(1L, result.getActiveTasks());
        assertNotNull(result.getTasksByStatus());
        assertNotNull(result.getTasksByPriority());
        assertEquals(1L, result.getTasksByStatus().get("OPEN"));
        assertEquals(1L, result.getTasksByStatus().get("DONE"));
        assertEquals(2L, result.getTasksByPriority().get("HIGH"));
    }

    @Test
    void priorityTypeService_getAllPriorityTypes_Success() {
        // Act
        List<PriorityTypeResponseDTO> result = priorityTypeService.getAllPriorityTypes();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(p -> "HIGH".equals(p.getType())));
        assertTrue(result.stream().anyMatch(p -> "MEDIUM".equals(p.getType())));
        assertTrue(result.stream().anyMatch(p -> "LOW".equals(p.getType())));
    }

    @Test
    void priorityTypeService_getPriorityTypeById_Success() {
        // Act
        PriorityTypeResponseDTO result = priorityTypeService.getPriorityTypeById(highPriority.getId());

        // Assert
        assertNotNull(result);
        assertEquals(highPriority.getId(), result.getId());
        assertEquals("HIGH", result.getType());
    }

    @Test
    void taskStatusTypeService_getAllTaskStatusTypes_Success() {
        // Act
        List<TaskStatusTypeResponseDTO> result = taskStatusTypeService.getAllTaskStatusTypes();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.stream().anyMatch(s -> "OPEN".equals(s.getType())));
        assertTrue(result.stream().anyMatch(s -> "IN_PROGRESS".equals(s.getType())));
        assertTrue(result.stream().anyMatch(s -> "DONE".equals(s.getType())));
        assertTrue(result.stream().anyMatch(s -> "CLOSED".equals(s.getType())));
    }

    @Test
    void taskStatusTypeService_getTaskStatusTypeById_Success() {
        // Act
        TaskStatusTypeResponseDTO result = taskStatusTypeService.getTaskStatusTypeById(openStatus.getId());

        // Assert
        assertNotNull(result);
        assertEquals(openStatus.getId(), result.getId());
        assertEquals("OPEN", result.getType());
    }
}
