package com.dilshan.coveragex.util;

import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.entity.Task;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.repository.PriorityTypeRepository;
import com.dilshan.coveragex.repository.TaskRepository;
import com.dilshan.coveragex.repository.TaskStatusTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final PriorityTypeRepository priorityTypeRepository;
    private final TaskStatusTypeRepository taskStatusTypeRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public DataInitializer(PriorityTypeRepository priorityTypeRepository,
                          TaskStatusTypeRepository taskStatusTypeRepository,
                          TaskRepository taskRepository) {
        this.priorityTypeRepository = priorityTypeRepository;
        this.taskStatusTypeRepository = taskStatusTypeRepository;
        this.taskRepository = taskRepository;
    }


    @Override
    public void run(String... args) {
        LOGGER.info("Starting data initialization...");
        
        initializePriorityTypes();
        initializeTaskStatusTypes();
        initializeSampleTasks();
        
        LOGGER.info("Data initialization completed successfully");
    }


    private void initializePriorityTypes() {
        LOGGER.info("Initializing priority types...");
        
        if (priorityTypeRepository.count() == 0) {
            List<PriorityType> priorityTypes = Arrays.asList(
                    new PriorityType(PriorityType.PriorityTypeEnum.HIGH.getValue()),
                    new PriorityType(PriorityType.PriorityTypeEnum.MEDIUM.getValue()),
                    new PriorityType(PriorityType.PriorityTypeEnum.LOW.getValue())
            );
            
            priorityTypeRepository.saveAll(priorityTypes);
            LOGGER.info("Created {} priority types", priorityTypes.size());
        } else {
            LOGGER.info("Priority types already exist, skipping initialization");
        }
    }

    private void initializeTaskStatusTypes() {
        LOGGER.info("Initializing task status types...");
        
        if (taskStatusTypeRepository.count() == 0) {
            List<TaskStatusType> statusTypes = Arrays.asList(
                    new TaskStatusType(TaskStatusType.TaskStatusTypeEnum.OPEN.getValue()),
                    new TaskStatusType(TaskStatusType.TaskStatusTypeEnum.IN_PROGRESS.getValue()),
                    new TaskStatusType(TaskStatusType.TaskStatusTypeEnum.HOLD.getValue()),
                    new TaskStatusType(TaskStatusType.TaskStatusTypeEnum.DONE.getValue()),
                    new TaskStatusType(TaskStatusType.TaskStatusTypeEnum.CLOSED.getValue())
            );
            
            taskStatusTypeRepository.saveAll(statusTypes);
            LOGGER.info("Created {} task status types", statusTypes.size());
        } else {
            LOGGER.info("Task status types already exist, skipping initialization");
        }
    }

    private void initializeSampleTasks() {
        LOGGER.info("Initializing sample tasks...");
        
        if (taskRepository.count() == 0) {
            // Get reference data
            PriorityType highPriority = priorityTypeRepository.findByType("HIGH").orElse(null);
            PriorityType mediumPriority = priorityTypeRepository.findByType("MEDIUM").orElse(null);
            PriorityType lowPriority = priorityTypeRepository.findByType("LOW").orElse(null);
            
            TaskStatusType openStatus = taskStatusTypeRepository.findByType("OPEN").orElse(null);
            TaskStatusType inProgressStatus = taskStatusTypeRepository.findByType("IN_PROGRESS").orElse(null);
            TaskStatusType doneStatus = taskStatusTypeRepository.findByType("DONE").orElse(null);
            
            if (highPriority != null && mediumPriority != null && lowPriority != null &&
                openStatus != null && inProgressStatus != null && doneStatus != null) {
                
                List<Task> sampleTasks = Arrays.asList(
                        new Task("Complete project documentation", 
                                "Write comprehensive documentation for the todo application including API docs and user guide", 
                                highPriority, inProgressStatus),
                        new Task("Implement user authentication", 
                                "Add JWT-based authentication system with login and registration", 
                                highPriority, openStatus),
                        new Task("Set up CI/CD pipeline", 
                                "Configure GitHub Actions for automated testing and deployment", 
                                mediumPriority, openStatus),
                        new Task("Write unit tests", 
                                "Create comprehensive unit tests for service layer methods", 
                                mediumPriority, inProgressStatus),
                        new Task("Design database schema", 
                                "Create optimized database schema with proper relationships and indexes", 
                                highPriority, doneStatus),
                        new Task("Create frontend components", 
                                "Develop React components for task management interface", 
                                mediumPriority, openStatus),
                        new Task("Implement search functionality", 
                                "Add full-text search capability for tasks", 
                                lowPriority, openStatus),
                        new Task("Add email notifications", 
                                "Send email notifications for task status changes", 
                                lowPriority, openStatus),
                        new Task("Performance optimization", 
                                "Optimize database queries and add caching where appropriate", 
                                mediumPriority, openStatus),
                        new Task("Security audit", 
                                "Conduct security review and implement necessary fixes", 
                                highPriority, openStatus)
                );
                
                taskRepository.saveAll(sampleTasks);
                LOGGER.info("Created {} sample tasks", sampleTasks.size());
            } else {
                LOGGER.warn("Could not create sample tasks - reference data not found");
            }
        } else {
            LOGGER.info("Tasks already exist, skipping sample data initialization");
        }
    }
}
