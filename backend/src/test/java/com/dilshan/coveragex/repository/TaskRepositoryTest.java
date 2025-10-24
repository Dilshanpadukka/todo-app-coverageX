package com.dilshan.coveragex.repository;

import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.Task;
import com.dilshan.coveragex.entity.TaskStatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private PriorityType highPriority;
    private PriorityType mediumPriority;
    private TaskStatusType openStatus;
    private TaskStatusType inProgressStatus;
    private TaskStatusType doneStatus;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        // Create and persist priority types
        highPriority = new PriorityType("HIGH");
        mediumPriority = new PriorityType("MEDIUM");
        entityManager.persistAndFlush(highPriority);
        entityManager.persistAndFlush(mediumPriority);

        // Create and persist task status types
        openStatus = new TaskStatusType("OPEN");
        inProgressStatus = new TaskStatusType("IN_PROGRESS");
        doneStatus = new TaskStatusType("DONE");
        entityManager.persistAndFlush(openStatus);
        entityManager.persistAndFlush(inProgressStatus);
        entityManager.persistAndFlush(doneStatus);

        // Create and persist tasks
        task1 = new Task("First Task", "Description for first task", highPriority, openStatus);
        task2 = new Task("Second Task", "Description for second task", mediumPriority, inProgressStatus);
        task3 = new Task("Third Task", "Another description", highPriority, doneStatus);
        
        entityManager.persistAndFlush(task1);
        entityManager.persistAndFlush(task2);
        entityManager.persistAndFlush(task3);
        
        entityManager.clear();
    }

    @Test
    void findByTaskStatusId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findByTaskStatusId(openStatus.getId(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("First Task", result.getContent().get(0).getTaskTitle());
        assertEquals(openStatus.getId(), result.getContent().get(0).getTaskStatus().getId());
    }

    @Test
    void findByPriorityId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findByPriorityId(highPriority.getId(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream()
                .allMatch(task -> task.getPriority().getId().equals(highPriority.getId())));
    }

    @Test
    void searchByTitleOrDescription_ByTitle() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.searchByTitleOrDescription("First", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("First Task", result.getContent().get(0).getTaskTitle());
    }

    @Test
    void searchByTitleOrDescription_ByDescription() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.searchByTitleOrDescription("Another", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Third Task", result.getContent().get(0).getTaskTitle());
    }

    @Test
    void searchByTitleOrDescription_CaseInsensitive() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.searchByTitleOrDescription("FIRST", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("First Task", result.getContent().get(0).getTaskTitle());
    }

    @Test
    void searchByTitleOrDescription_NoResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.searchByTitleOrDescription("NonExistent", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
    }

    @Test
    void countTasksByStatus_Success() {
        // Act
        List<Object[]> result = taskRepository.countTasksByStatus();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify counts for each status
        boolean foundOpen = false, foundInProgress = false, foundDone = false;
        for (Object[] row : result) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            
            switch (status) {
                case "OPEN":
                    assertEquals(1L, count);
                    foundOpen = true;
                    break;
                case "IN_PROGRESS":
                    assertEquals(1L, count);
                    foundInProgress = true;
                    break;
                case "DONE":
                    assertEquals(1L, count);
                    foundDone = true;
                    break;
            }
        }
        
        assertTrue(foundOpen && foundInProgress && foundDone);
    }

    @Test
    void countTasksByPriority_Success() {
        // Act
        List<Object[]> result = taskRepository.countTasksByPriority();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify counts for each priority
        boolean foundHigh = false, foundMedium = false;
        for (Object[] row : result) {
            String priority = (String) row[0];
            Long count = (Long) row[1];
            
            switch (priority) {
                case "HIGH":
                    assertEquals(2L, count);
                    foundHigh = true;
                    break;
                case "MEDIUM":
                    assertEquals(1L, count);
                    foundMedium = true;
                    break;
            }
        }
        
        assertTrue(foundHigh && foundMedium);
    }

    @Test
    void countCompletedTasks_Success() {
        // Act
        Long result = taskRepository.countCompletedTasks();

        // Assert
        assertNotNull(result);
        assertEquals(1L, result); // Only task3 has DONE status
    }

    @Test
    void countActiveTasks_Success() {
        // Act
        Long result = taskRepository.countActiveTasks();

        // Assert
        assertNotNull(result);
        assertEquals(2L, result); // task1 (OPEN) and task2 (IN_PROGRESS)
    }

    @Test
    void findTasksWithFilters_AllFilters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findTasksWithFilters(
                openStatus.getId(), highPriority.getId(), "First", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("First Task", result.getContent().get(0).getTaskTitle());
    }

    @Test
    void findTasksWithFilters_StatusOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findTasksWithFilters(
                openStatus.getId(), null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("First Task", result.getContent().get(0).getTaskTitle());
    }

    @Test
    void findTasksWithFilters_PriorityOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findTasksWithFilters(
                null, highPriority.getId(), null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream()
                .allMatch(task -> task.getPriority().getId().equals(highPriority.getId())));
    }

    @Test
    void findTasksWithFilters_SearchTermOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findTasksWithFilters(
                null, null, "description", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size()); // All tasks have "description" in their description
    }

    @Test
    void findTasksWithFilters_NoFilters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findTasksWithFilters(
                null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size()); // All tasks should be returned
    }

    @Test
    void findTasksWithFilters_NoResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> result = taskRepository.findTasksWithFilters(
                openStatus.getId(), mediumPriority.getId(), null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size()); // No task matches OPEN status AND MEDIUM priority
    }
}
