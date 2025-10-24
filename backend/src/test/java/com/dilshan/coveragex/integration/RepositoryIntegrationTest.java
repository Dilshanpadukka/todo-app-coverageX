package com.dilshan.coveragex.integration;

import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.Task;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.repository.PriorityTypeRepository;
import com.dilshan.coveragex.repository.TaskRepository;
import com.dilshan.coveragex.repository.TaskStatusTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Repository layer.
 * Tests the repository implementations with real database interactions.
 * 
 * @author Dilshan
 * @version 1.0
 * @since 2025-10-24
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class RepositoryIntegrationTest {

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

        // Clear and recreate reference data
        priorityTypeRepository.deleteAll();
        taskStatusTypeRepository.deleteAll();

        // Setup test data with unique names per test
        String testId = String.valueOf(System.nanoTime());
        highPriority = priorityTypeRepository.save(new PriorityType("HIGH_" + testId));
        mediumPriority = priorityTypeRepository.save(new PriorityType("MEDIUM_" + testId));
        PriorityType lowPriority = priorityTypeRepository.save(new PriorityType("LOW_" + testId));

        openStatus = taskStatusTypeRepository.save(new TaskStatusType("OPEN_" + testId));
        inProgressStatus = taskStatusTypeRepository.save(new TaskStatusType("IN_PROGRESS_" + testId));
        doneStatus = taskStatusTypeRepository.save(new TaskStatusType("DONE_" + testId));
        TaskStatusType closedStatus = taskStatusTypeRepository.save(new TaskStatusType("CLOSED_" + testId));
    }

    @Test
    void priorityTypeRepository_findAll_Success() {
        // Act
        List<PriorityType> result = priorityTypeRepository.findAll();

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getType().startsWith("HIGH")));
        assertTrue(result.stream().anyMatch(p -> p.getType().startsWith("MEDIUM")));
        assertTrue(result.stream().anyMatch(p -> p.getType().startsWith("LOW")));
    }

    @Test
    void taskStatusTypeRepository_findByType_Success() {
        // Act
        Optional<TaskStatusType> result = taskStatusTypeRepository.findByType(openStatus.getType());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(openStatus.getType(), result.get().getType());
        assertEquals(openStatus.getId(), result.get().getId());
    }

    @Test
    void priorityTypeRepository_findByType_Success() {
        // Act
        Optional<PriorityType> result = priorityTypeRepository.findByType(highPriority.getType());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(highPriority.getType(), result.get().getType());
        assertEquals(highPriority.getId(), result.get().getId());
    }

    @Test
    void taskRepository_saveAndFind_Success() {
        // Arrange
        Task task = new Task("Test Task", "Test Description", highPriority, openStatus);

        // Act
        Task savedTask = taskRepository.save(task);
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getTaskTitle());
        assertEquals("Test Description", foundTask.get().getDescription());
        assertEquals(highPriority.getId(), foundTask.get().getPriority().getId());
        assertEquals(openStatus.getId(), foundTask.get().getTaskStatus().getId());
        assertNotNull(foundTask.get().getCreateDate());
        assertNotNull(foundTask.get().getLastStatusChangeDate());
    }

    @Test
    void taskRepository_findByTaskStatusId_Success() {
        // Arrange
        Task openTask = taskRepository.save(new Task("Open Task", "Description", highPriority, openStatus));
        Task inProgressTask = taskRepository.save(new Task("In Progress Task", "Description", highPriority, inProgressStatus));
        
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findByTaskStatusId(openStatus.getId(), pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Open Task", result.getContent().get(0).getTaskTitle());
        assertEquals(openStatus.getId(), result.getContent().get(0).getTaskStatus().getId());
    }

    @Test
    void taskRepository_findByPriorityId_Success() {
        // Arrange
        Task highPriorityTask1 = taskRepository.save(new Task("High Priority Task 1", "Description", highPriority, openStatus));
        Task highPriorityTask2 = taskRepository.save(new Task("High Priority Task 2", "Description", highPriority, inProgressStatus));
        Task mediumPriorityTask = taskRepository.save(new Task("Medium Priority Task", "Description", mediumPriority, openStatus));
        
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findByPriorityId(highPriority.getId(), pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(task -> 
            task.getPriority().getId().equals(highPriority.getId())));
    }

    @Test
    void taskRepository_searchByTitleOrDescription_Success() {
        // Arrange
        Task searchableTask = taskRepository.save(new Task("Searchable Task", "Normal description", highPriority, openStatus));
        Task anotherTask = taskRepository.save(new Task("Normal Task", "Searchable description", highPriority, openStatus));
        Task normalTask = taskRepository.save(new Task("Normal Task", "Normal description", highPriority, openStatus));
        
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.searchByTitleOrDescription("Searchable", pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().anyMatch(task -> 
            task.getTaskTitle().contains("Searchable")));
        assertTrue(result.getContent().stream().anyMatch(task -> 
            task.getDescription().contains("Searchable")));
    }

    @Test
    void taskRepository_countTasksByStatus_Success() {
        // Arrange
        taskRepository.save(new Task("Open Task 1", "Description", highPriority, openStatus));
        taskRepository.save(new Task("Open Task 2", "Description", highPriority, openStatus));
        taskRepository.save(new Task("In Progress Task", "Description", highPriority, inProgressStatus));
        taskRepository.save(new Task("Done Task", "Description", highPriority, doneStatus));

        // Act
        List<Object[]> result = taskRepository.countTasksByStatus();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size()); // 3 different statuses used
        
        // Verify counts
        for (Object[] row : result) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            
            switch (status) {
                case "OPEN":
                    assertEquals(2L, count);
                    break;
                case "IN_PROGRESS":
                    assertEquals(1L, count);
                    break;
                case "DONE":
                    assertEquals(1L, count);
                    break;
            }
        }
    }

    @Test
    void taskRepository_countTasksByPriority_Success() {
        // Arrange
        taskRepository.save(new Task("High Priority Task 1", "Description", highPriority, openStatus));
        taskRepository.save(new Task("High Priority Task 2", "Description", highPriority, openStatus));
        taskRepository.save(new Task("Medium Priority Task", "Description", mediumPriority, openStatus));

        // Act
        List<Object[]> result = taskRepository.countTasksByPriority();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // 2 different priorities used
        
        // Verify counts
        for (Object[] row : result) {
            String priority = (String) row[0];
            Long count = (Long) row[1];
            
            switch (priority) {
                case "HIGH":
                    assertEquals(2L, count);
                    break;
                case "MEDIUM":
                    assertEquals(1L, count);
                    break;
            }
        }
    }

    @Test
    void taskRepository_countCompletedTasks_Success() {
        // Arrange - Create status types that match the hardcoded query, check if exists first
        TaskStatusType doneStatusForCount = taskStatusTypeRepository.findByType("DONE")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("DONE")));
        TaskStatusType openStatusForCount = taskStatusTypeRepository.findByType("OPEN")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("OPEN")));

        taskRepository.save(new Task("Open Task", "Description", highPriority, openStatusForCount));
        taskRepository.save(new Task("Done Task", "Description", highPriority, doneStatusForCount));

        // Act
        Long result = taskRepository.countCompletedTasks();

        // Assert
        assertEquals(1L, result); // Only DONE status is considered completed
    }

    @Test
    void taskRepository_countActiveTasks_Success() {
        // Arrange - Create status types that match the hardcoded query, check if exists first
        TaskStatusType openStatusForCount = taskStatusTypeRepository.findByType("OPEN")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("OPEN")));
        TaskStatusType inProgressStatusForCount = taskStatusTypeRepository.findByType("IN_PROGRESS")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("IN_PROGRESS")));
        TaskStatusType doneStatusForCount = taskStatusTypeRepository.findByType("DONE")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("DONE")));

        taskRepository.save(new Task("Open Task", "Description", highPriority, openStatusForCount));
        taskRepository.save(new Task("In Progress Task", "Description", highPriority, inProgressStatusForCount));
        taskRepository.save(new Task("Done Task", "Description", highPriority, doneStatusForCount));

        // Act
        Long result = taskRepository.countActiveTasks();

        // Assert
        assertEquals(2L, result); // OPEN and IN_PROGRESS are considered active
    }

    @Test
    void taskRepository_findTasksWithFilters_Success() {
        // Arrange
        Task matchingTask = taskRepository.save(new Task("Filtered Task", "Searchable description", highPriority, openStatus));
        Task nonMatchingTask1 = taskRepository.save(new Task("Other Task", "Other description", mediumPriority, openStatus));
        Task nonMatchingTask2 = taskRepository.save(new Task("Filtered Task", "Searchable description", highPriority, inProgressStatus));
        
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findTasksWithFilters(
            openStatus.getId(), highPriority.getId(), "Searchable", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Filtered Task", result.getContent().get(0).getTaskTitle());
        assertEquals(openStatus.getId(), result.getContent().get(0).getTaskStatus().getId());
        assertEquals(highPriority.getId(), result.getContent().get(0).getPriority().getId());
        assertTrue(result.getContent().get(0).getDescription().contains("Searchable"));
    }

    @Test
    void taskRepository_findTasksWithFilters_NullFilters_Success() {
        // Arrange
        taskRepository.save(new Task("Task 1", "Description 1", highPriority, openStatus));
        taskRepository.save(new Task("Task 2", "Description 2", mediumPriority, inProgressStatus));
        taskRepository.save(new Task("Task 3", "Description 3", highPriority, doneStatus));
        
        Pageable pageable = PageRequest.of(0, 10);

        // Act - No filters applied
        Page<Task> result = taskRepository.findTasksWithFilters(null, null, null, pageable);

        // Assert
        assertEquals(3, result.getContent().size()); // All tasks should be returned
    }

    @Test
    void taskRepository_delete_Success() {
        // Arrange
        Task task = taskRepository.save(new Task("Task to Delete", "Description", highPriority, openStatus));
        Long taskId = task.getId();
        assertTrue(taskRepository.existsById(taskId));

        // Act
        taskRepository.delete(task);

        // Assert
        assertFalse(taskRepository.existsById(taskId));
    }

    @Test
    void taskRepository_update_Success() {
        // Arrange
        Task task = taskRepository.save(new Task("Original Title", "Original Description", highPriority, openStatus));
        Long taskId = task.getId();

        // Act
        task.setTaskTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setPriority(mediumPriority);
        task.setTaskStatus(inProgressStatus);
        Task updatedTask = taskRepository.save(task);

        // Assert
        assertEquals(taskId, updatedTask.getId());
        assertEquals("Updated Title", updatedTask.getTaskTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(mediumPriority.getId(), updatedTask.getPriority().getId());
        assertEquals(inProgressStatus.getId(), updatedTask.getTaskStatus().getId());
        
        // Verify the changes are persisted
        Optional<Task> foundTask = taskRepository.findById(taskId);
        assertTrue(foundTask.isPresent());
        assertEquals("Updated Title", foundTask.get().getTaskTitle());
    }

    @Test
    void allRepositories_cascadeOperations_Success() {
        // Arrange
        Task task = taskRepository.save(new Task("Test Task", "Test Description", highPriority, openStatus));

        // Act - Delete priority type (should not affect existing tasks due to foreign key constraints)
        Optional<Task> foundTask = taskRepository.findById(task.getId());
        assertTrue(foundTask.isPresent());
        assertEquals(highPriority.getId(), foundTask.get().getPriority().getId());
        assertEquals(openStatus.getId(), foundTask.get().getTaskStatus().getId());

        // Verify relationships are maintained
        assertEquals(highPriority.getType(), foundTask.get().getPriority().getType());
        assertEquals(openStatus.getType(), foundTask.get().getTaskStatus().getType());
    }
}
