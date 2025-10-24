package com.dilshan.coveragex.repository;

import com.dilshan.coveragex.entity.TaskStatusType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskStatusTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskStatusTypeRepository taskStatusTypeRepository;

    private TaskStatusType openStatus;
    private TaskStatusType inProgressStatus;
    private TaskStatusType holdStatus;
    private TaskStatusType doneStatus;
    private TaskStatusType closedStatus;

    @BeforeEach
    void setUp() {
        // Create and persist task status types
        openStatus = new TaskStatusType("OPEN");
        inProgressStatus = new TaskStatusType("IN_PROGRESS");
        holdStatus = new TaskStatusType("HOLD");
        doneStatus = new TaskStatusType("DONE");
        closedStatus = new TaskStatusType("CLOSED");
        
        entityManager.persistAndFlush(openStatus);
        entityManager.persistAndFlush(inProgressStatus);
        entityManager.persistAndFlush(holdStatus);
        entityManager.persistAndFlush(doneStatus);
        entityManager.persistAndFlush(closedStatus);
        
        entityManager.clear();
    }

    @Test
    void findAll_Success() {
        // Act
        List<TaskStatusType> result = taskStatusTypeRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        
        // Verify all status types are present
        assertTrue(result.stream().anyMatch(s -> "OPEN".equals(s.getType())));
        assertTrue(result.stream().anyMatch(s -> "IN_PROGRESS".equals(s.getType())));
        assertTrue(result.stream().anyMatch(s -> "HOLD".equals(s.getType())));
        assertTrue(result.stream().anyMatch(s -> "DONE".equals(s.getType())));
        assertTrue(result.stream().anyMatch(s -> "CLOSED".equals(s.getType())));
    }

    @Test
    void findById_Success() {
        // Act
        Optional<TaskStatusType> result = taskStatusTypeRepository.findById(openStatus.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("OPEN", result.get().getType());
        assertEquals(openStatus.getId(), result.get().getId());
    }

    @Test
    void findById_NotFound() {
        // Act
        Optional<TaskStatusType> result = taskStatusTypeRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByType_Success() {
        // Act
        Optional<TaskStatusType> result = taskStatusTypeRepository.findByType("OPEN");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("OPEN", result.get().getType());
        assertEquals(openStatus.getId(), result.get().getId());
    }

    @Test
    void findByType_NotFound() {
        // Act
        Optional<TaskStatusType> result = taskStatusTypeRepository.findByType("INVALID");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByType_CaseSensitive() {
        // Act
        Optional<TaskStatusType> result = taskStatusTypeRepository.findByType("open");

        // Assert
        assertFalse(result.isPresent()); // Should be case sensitive
    }

    @Test
    void existsById_Success() {
        // Act
        boolean result = taskStatusTypeRepository.existsById(openStatus.getId());

        // Assert
        assertTrue(result);
    }

    @Test
    void existsById_NotExists() {
        // Act
        boolean result = taskStatusTypeRepository.existsById(999L);

        // Assert
        assertFalse(result);
    }

    @Test
    void save_Success() {
        // Arrange
        TaskStatusType newStatus = new TaskStatusType("CANCELLED");

        // Act
        TaskStatusType saved = taskStatusTypeRepository.save(newStatus);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("CANCELLED", saved.getType());
        
        // Verify it's persisted
        Optional<TaskStatusType> found = taskStatusTypeRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("CANCELLED", found.get().getType());
    }

    @Test
    void delete_Success() {
        // Arrange
        Long statusId = openStatus.getId();
        assertTrue(taskStatusTypeRepository.existsById(statusId));

        // Act
        taskStatusTypeRepository.deleteById(statusId);

        // Assert
        assertFalse(taskStatusTypeRepository.existsById(statusId));
        
        // Verify total count decreased
        List<TaskStatusType> remaining = taskStatusTypeRepository.findAll();
        assertEquals(4, remaining.size());
    }

    @Test
    void count_Success() {
        // Act
        long count = taskStatusTypeRepository.count();

        // Assert
        assertEquals(5L, count);
    }

    @Test
    void findByType_AllTypes() {
        // Test all predefined status types
        Optional<TaskStatusType> open = taskStatusTypeRepository.findByType("OPEN");
        Optional<TaskStatusType> inProgress = taskStatusTypeRepository.findByType("IN_PROGRESS");
        Optional<TaskStatusType> hold = taskStatusTypeRepository.findByType("HOLD");
        Optional<TaskStatusType> done = taskStatusTypeRepository.findByType("DONE");
        Optional<TaskStatusType> closed = taskStatusTypeRepository.findByType("CLOSED");

        // Assert
        assertTrue(open.isPresent());
        assertTrue(inProgress.isPresent());
        assertTrue(hold.isPresent());
        assertTrue(done.isPresent());
        assertTrue(closed.isPresent());
        
        assertEquals("OPEN", open.get().getType());
        assertEquals("IN_PROGRESS", inProgress.get().getType());
        assertEquals("HOLD", hold.get().getType());
        assertEquals("DONE", done.get().getType());
        assertEquals("CLOSED", closed.get().getType());
    }

    @Test
    void save_UpdateExisting() {
        // Arrange
        TaskStatusType existing = taskStatusTypeRepository.findById(openStatus.getId()).orElseThrow();
        existing.setType("PENDING");

        // Act
        TaskStatusType updated = taskStatusTypeRepository.save(existing);

        // Assert
        assertNotNull(updated);
        assertEquals(openStatus.getId(), updated.getId());
        assertEquals("PENDING", updated.getType());
        
        // Verify the change is persisted
        Optional<TaskStatusType> found = taskStatusTypeRepository.findById(openStatus.getId());
        assertTrue(found.isPresent());
        assertEquals("PENDING", found.get().getType());
    }

    @Test
    void findAll_OrderConsistency() {
        // Act - Call multiple times to check consistency
        List<TaskStatusType> result1 = taskStatusTypeRepository.findAll();
        List<TaskStatusType> result2 = taskStatusTypeRepository.findAll();

        // Assert
        assertEquals(result1.size(), result2.size());
        assertEquals(5, result1.size());
        
        // Verify same entities are returned (though order might vary)
        for (TaskStatusType s1 : result1) {
            assertTrue(result2.stream().anyMatch(s2 -> s2.getId().equals(s1.getId())));
        }
    }

    @Test
    void findByType_WithUnderscores() {
        // Test status type with underscores
        Optional<TaskStatusType> result = taskStatusTypeRepository.findByType("IN_PROGRESS");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("IN_PROGRESS", result.get().getType());
        assertEquals(inProgressStatus.getId(), result.get().getId());
    }

    @Test
    void save_DuplicateType_ShouldFail() {
        // Arrange
        TaskStatusType duplicateStatus = new TaskStatusType("OPEN");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            taskStatusTypeRepository.saveAndFlush(duplicateStatus);
        });
    }
}
