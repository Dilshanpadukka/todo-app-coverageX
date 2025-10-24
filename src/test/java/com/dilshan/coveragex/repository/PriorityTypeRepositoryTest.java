package com.dilshan.coveragex.repository;

import com.dilshan.coveragex.entity.PriorityType;
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
class PriorityTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PriorityTypeRepository priorityTypeRepository;

    private PriorityType highPriority;
    private PriorityType mediumPriority;
    private PriorityType lowPriority;

    @BeforeEach
    void setUp() {
        // Create and persist priority types
        highPriority = new PriorityType("HIGH");
        mediumPriority = new PriorityType("MEDIUM");
        lowPriority = new PriorityType("LOW");
        
        entityManager.persistAndFlush(highPriority);
        entityManager.persistAndFlush(mediumPriority);
        entityManager.persistAndFlush(lowPriority);
        
        entityManager.clear();
    }

    @Test
    void findAll_Success() {
        // Act
        List<PriorityType> result = priorityTypeRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify all priority types are present
        assertTrue(result.stream().anyMatch(p -> "HIGH".equals(p.getType())));
        assertTrue(result.stream().anyMatch(p -> "MEDIUM".equals(p.getType())));
        assertTrue(result.stream().anyMatch(p -> "LOW".equals(p.getType())));
    }

    @Test
    void findById_Success() {
        // Act
        Optional<PriorityType> result = priorityTypeRepository.findById(highPriority.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("HIGH", result.get().getType());
        assertEquals(highPriority.getId(), result.get().getId());
    }

    @Test
    void findById_NotFound() {
        // Act
        Optional<PriorityType> result = priorityTypeRepository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByType_Success() {
        // Act
        Optional<PriorityType> result = priorityTypeRepository.findByType("HIGH");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("HIGH", result.get().getType());
        assertEquals(highPriority.getId(), result.get().getId());
    }

    @Test
    void findByType_NotFound() {
        // Act
        Optional<PriorityType> result = priorityTypeRepository.findByType("INVALID");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByType_CaseSensitive() {
        // Act
        Optional<PriorityType> result = priorityTypeRepository.findByType("high");

        // Assert
        assertFalse(result.isPresent()); // Should be case sensitive
    }

    @Test
    void existsById_Success() {
        // Act
        boolean result = priorityTypeRepository.existsById(highPriority.getId());

        // Assert
        assertTrue(result);
    }

    @Test
    void existsById_NotExists() {
        // Act
        boolean result = priorityTypeRepository.existsById(999L);

        // Assert
        assertFalse(result);
    }

    @Test
    void save_Success() {
        // Arrange
        PriorityType newPriority = new PriorityType("URGENT");

        // Act
        PriorityType saved = priorityTypeRepository.save(newPriority);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("URGENT", saved.getType());
        
        // Verify it's persisted
        Optional<PriorityType> found = priorityTypeRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("URGENT", found.get().getType());
    }

    @Test
    void delete_Success() {
        // Arrange
        Long priorityId = highPriority.getId();
        assertTrue(priorityTypeRepository.existsById(priorityId));

        // Act
        priorityTypeRepository.deleteById(priorityId);

        // Assert
        assertFalse(priorityTypeRepository.existsById(priorityId));
        
        // Verify total count decreased
        List<PriorityType> remaining = priorityTypeRepository.findAll();
        assertEquals(2, remaining.size());
    }

    @Test
    void count_Success() {
        // Act
        long count = priorityTypeRepository.count();

        // Assert
        assertEquals(3L, count);
    }

    @Test
    void findByType_AllTypes() {
        // Test all predefined priority types
        Optional<PriorityType> high = priorityTypeRepository.findByType("HIGH");
        Optional<PriorityType> medium = priorityTypeRepository.findByType("MEDIUM");
        Optional<PriorityType> low = priorityTypeRepository.findByType("LOW");

        // Assert
        assertTrue(high.isPresent());
        assertTrue(medium.isPresent());
        assertTrue(low.isPresent());
        
        assertEquals("HIGH", high.get().getType());
        assertEquals("MEDIUM", medium.get().getType());
        assertEquals("LOW", low.get().getType());
    }

    @Test
    void save_UpdateExisting() {
        // Arrange
        PriorityType existing = priorityTypeRepository.findById(highPriority.getId()).orElseThrow();
        existing.setType("CRITICAL");

        // Act
        PriorityType updated = priorityTypeRepository.save(existing);

        // Assert
        assertNotNull(updated);
        assertEquals(highPriority.getId(), updated.getId());
        assertEquals("CRITICAL", updated.getType());
        
        // Verify the change is persisted
        Optional<PriorityType> found = priorityTypeRepository.findById(highPriority.getId());
        assertTrue(found.isPresent());
        assertEquals("CRITICAL", found.get().getType());
    }

    @Test
    void findAll_OrderConsistency() {
        // Act - Call multiple times to check consistency
        List<PriorityType> result1 = priorityTypeRepository.findAll();
        List<PriorityType> result2 = priorityTypeRepository.findAll();

        // Assert
        assertEquals(result1.size(), result2.size());
        assertEquals(3, result1.size());
        
        // Verify same entities are returned (though order might vary)
        for (PriorityType p1 : result1) {
            assertTrue(result2.stream().anyMatch(p2 -> p2.getId().equals(p1.getId())));
        }
    }
}
