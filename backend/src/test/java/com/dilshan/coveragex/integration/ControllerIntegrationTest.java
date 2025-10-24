package com.dilshan.coveragex.integration;

import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.Task;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.repository.PriorityTypeRepository;
import com.dilshan.coveragex.repository.TaskRepository;
import com.dilshan.coveragex.repository.TaskStatusTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PriorityTypeRepository priorityTypeRepository;

    @Autowired
    private TaskStatusTypeRepository taskStatusTypeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private PriorityType highPriority;
    private PriorityType mediumPriority;
    private TaskStatusType openStatus;
    private TaskStatusType inProgressStatus;
    private TaskStatusType doneStatus;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up existing data
        taskRepository.deleteAll();
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
        TaskStatusType closedStatus = taskStatusTypeRepository.findByType("CLOSED")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("CLOSED")));
    }

    @Test
    void taskController_createTask_Success() throws Exception {
        // Arrange
        String taskJson = """
            {
                "taskTitle": "Integration Test Task",
                "description": "Test Description",
                "priorityId": %d,
                "taskStatusId": %d
            }
            """.formatted(highPriority.getId(), openStatus.getId());

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskTitle").value("Integration Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.priority.type").value(highPriority.getType()))
                .andExpect(jsonPath("$.taskStatus.type").value(openStatus.getType()));
    }

    @Test
    void taskController_createTask_ValidationError() throws Exception {
        // Arrange - Invalid task with empty title
        String taskJson = """
            {
                "taskTitle": "",
                "description": "Test Description",
                "priorityId": %d,
                "taskStatusId": %d
            }
            """.formatted(highPriority.getId(), openStatus.getId());

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void taskController_getAllTasks_Success() throws Exception {
        // Arrange
        Task task1 = taskRepository.save(new Task("Task 1", "Description 1", highPriority, openStatus));
        Task task2 = taskRepository.save(new Task("Task 2", "Description 2", mediumPriority, inProgressStatus));

        // Act & Assert
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                // Don't check specific order as it depends on creation timestamps
                .andExpect(jsonPath("$.content[*].taskTitle").value(hasItems("Task 1", "Task 2")));
    }

    @Test
    void taskController_getTaskById_Success() throws Exception {
        // Arrange
        Task task = taskRepository.save(new Task("Test Task", "Test Description", highPriority, openStatus));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.taskTitle").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void taskController_getTaskById_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void taskController_updateTask_Success() throws Exception {
        // Arrange
        Task task = taskRepository.save(new Task("Original Task", "Original Description", highPriority, openStatus));
        
        String updateJson = """
            {
                "taskTitle": "Updated Task",
                "description": "Updated Description",
                "priorityId": %d,
                "taskStatusId": %d
            }
            """.formatted(mediumPriority.getId(), inProgressStatus.getId());

        // Act & Assert
        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskTitle").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.priority.type").value(mediumPriority.getType()))
                .andExpect(jsonPath("$.taskStatus.type").value(inProgressStatus.getType()));
    }

    @Test
    void taskController_deleteTask_Success() throws Exception {
        // Arrange - Create CLOSED status for soft delete, check if exists first
        TaskStatusType closedStatus = taskStatusTypeRepository.findByType("CLOSED")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("CLOSED")));
        Task task = taskRepository.save(new Task("Task to Delete", "Description", highPriority, openStatus));

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        // Verify soft deletion - task should still exist but with CLOSED status
        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskStatus.type").value("CLOSED"));
    }

    @Test
    void taskController_searchTasks_Success() throws Exception {
        // Arrange
        Task searchableTask = taskRepository.save(new Task("Searchable Task", "Normal description", highPriority, openStatus));
        Task normalTask = taskRepository.save(new Task("Normal Task", "Normal description", highPriority, openStatus));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/search")
                .param("searchTerm", "Searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].taskTitle").value("Searchable Task"));
    }

    @Test
    void taskController_getTasksByStatus_Success() throws Exception {
        // Arrange
        Task openTask = taskRepository.save(new Task("Open Task", "Description", highPriority, openStatus));
        Task inProgressTask = taskRepository.save(new Task("In Progress Task", "Description", highPriority, inProgressStatus));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/status/{statusId}", openStatus.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].taskTitle").value("Open Task"));
    }

    @Test
    void taskController_getTasksByPriority_Success() throws Exception {
        // Arrange
        Task highPriorityTask = taskRepository.save(new Task("High Priority Task", "Description", highPriority, openStatus));
        Task mediumPriorityTask = taskRepository.save(new Task("Medium Priority Task", "Description", mediumPriority, openStatus));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/priority/{priorityId}", highPriority.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].taskTitle").value("High Priority Task"));
    }

    @Test
    void taskController_getTaskStatistics_Success() throws Exception {
        // Arrange - Create status types that match the hardcoded count queries
        TaskStatusType openStatusForStats = taskStatusTypeRepository.findByType("OPEN")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("OPEN")));
        TaskStatusType inProgressStatusForStats = taskStatusTypeRepository.findByType("IN_PROGRESS")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("IN_PROGRESS")));
        TaskStatusType doneStatusForStats = taskStatusTypeRepository.findByType("DONE")
                .orElseGet(() -> taskStatusTypeRepository.save(new TaskStatusType("DONE")));

        taskRepository.save(new Task("Open Task", "Description", highPriority, openStatusForStats));
        taskRepository.save(new Task("In Progress Task", "Description", highPriority, inProgressStatusForStats));
        taskRepository.save(new Task("Done Task", "Description", mediumPriority, doneStatusForStats));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(3))
                .andExpect(jsonPath("$.tasksByStatus").exists())
                .andExpect(jsonPath("$.tasksByPriority").exists())
                .andExpect(jsonPath("$.completedTasks").value(1))
                .andExpect(jsonPath("$.activeTasks").value(2));
    }

    @Test
    void priorityTypeController_getAllPriorityTypes_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/priority-types")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void taskStatusTypeController_getTaskStatusTypeById_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/task-status-types/{id}", openStatus.getId())
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(openStatus.getId()))
                .andExpect(jsonPath("$.type").value(openStatus.getType())) // Use actual type from test data
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}
