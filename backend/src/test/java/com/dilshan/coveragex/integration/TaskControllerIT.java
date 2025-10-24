package com.dilshan.coveragex.integration;

import com.dilshan.coveragex.dto.TaskCreateRequestDTO;
import com.dilshan.coveragex.dto.TaskUpdateRequestDTO;
import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.repository.PriorityTypeRepository;
import com.dilshan.coveragex.repository.TaskRepository;
import com.dilshan.coveragex.repository.TaskStatusTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskControllerIT {

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
    void createTask_Success() throws Exception {
        // Arrange
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Integration Test Task");
        createRequest.setDescription("This is a test task for integration testing");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        String requestJson = objectMapper.writeValueAsString(createRequest);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskTitle", is("Integration Test Task")))
                .andExpect(jsonPath("$.description", is("This is a test task for integration testing")))
                .andExpect(jsonPath("$.priority.type", is("HIGH")))
                .andExpect(jsonPath("$.taskStatus.type", is("OPEN")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.createDate", notNullValue()));
    }

    @Test
    void createTask_ValidationError() throws Exception {
        // Arrange - Invalid request with blank title
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle(""); // Invalid
        createRequest.setDescription("Valid description");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        String requestJson = objectMapper.writeValueAsString(createRequest);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks_Success() throws Exception {
        // Arrange - Create a test task first
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Test Task");
        createRequest.setDescription("Test Description");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        String requestJson = objectMapper.writeValueAsString(createRequest);
        
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // Act & Assert
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].taskTitle", is("Test Task")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    void getTaskById_Success() throws Exception {
        // Arrange - Create a test task first
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Test Task");
        createRequest.setDescription("Test Description");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        String requestJson = objectMapper.writeValueAsString(createRequest);
        
        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract task ID from response
        Long taskId = objectMapper.readTree(response).get("id").asLong();

        // Act & Assert
        mockMvc.perform(get("/api/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(taskId.intValue())))
                .andExpect(jsonPath("$.taskTitle", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    void getTaskById_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_Success() throws Exception {
        // Arrange - Create a test task first
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Original Task");
        createRequest.setDescription("Original Description");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        String createJson = objectMapper.writeValueAsString(createRequest);
        
        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        // Prepare update request
        TaskUpdateRequestDTO updateRequest = new TaskUpdateRequestDTO();
        updateRequest.setTaskTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriorityId(mediumPriority.getId());
        updateRequest.setTaskStatusId(inProgressStatus.getId());

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(taskId.intValue())))
                .andExpect(jsonPath("$.taskTitle", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.priority.type", is("MEDIUM")))
                .andExpect(jsonPath("$.taskStatus.type", is("IN_PROGRESS")));
    }

    @Test
    void deleteTask_Success() throws Exception {
        // Arrange - Create a test task first
        TaskCreateRequestDTO createRequest = new TaskCreateRequestDTO();
        createRequest.setTaskTitle("Task to Delete");
        createRequest.setDescription("This task will be deleted");
        createRequest.setPriorityId(highPriority.getId());
        createRequest.setTaskStatusId(openStatus.getId());

        String requestJson = objectMapper.writeValueAsString(createRequest);
        
        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/" + taskId))
                .andExpect(status().isNoContent());

        // Verify task is deleted
        mockMvc.perform(get("/api/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksByStatus_Success() throws Exception {
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

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(openTask)));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inProgressTask)));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/status/" + openStatus.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].taskTitle", is("Open Task")))
                .andExpect(jsonPath("$.content[0].taskStatus.type", is("OPEN")));
    }

    @Test
    void getTasksByPriority_Success() throws Exception {
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

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(highPriorityTask)));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mediumPriorityTask)));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/priority/" + highPriority.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].taskTitle", is("High Priority Task")))
                .andExpect(jsonPath("$.content[0].priority.type", is("HIGH")));
    }

    @Test
    void searchTasks_Success() throws Exception {
        // Arrange - Create searchable tasks
        TaskCreateRequestDTO searchableTask = new TaskCreateRequestDTO();
        searchableTask.setTaskTitle("Searchable Task");
        searchableTask.setDescription("This task contains searchable content");
        searchableTask.setPriorityId(highPriority.getId());
        searchableTask.setTaskStatusId(openStatus.getId());

        TaskCreateRequestDTO otherTask = new TaskCreateRequestDTO();
        otherTask.setTaskTitle("Other Task");
        otherTask.setDescription("This task has different content");
        otherTask.setPriorityId(highPriority.getId());
        otherTask.setTaskStatusId(openStatus.getId());

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchableTask)));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otherTask)));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/search")
                        .param("searchTerm", "Searchable"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].taskTitle", is("Searchable Task")));
    }

    @Test
    void getTaskStatistics_Success() throws Exception {
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

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(openHighTask)));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doneHighTask)));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalTasks", is(2)))
                .andExpect(jsonPath("$.completedTasks", is(1)))
                .andExpect(jsonPath("$.activeTasks", is(1)))
                .andExpect(jsonPath("$.tasksByStatus.OPEN", is(1)))
                .andExpect(jsonPath("$.tasksByStatus.DONE", is(1)))
                .andExpect(jsonPath("$.tasksByPriority.HIGH", is(2)));
    }
}
