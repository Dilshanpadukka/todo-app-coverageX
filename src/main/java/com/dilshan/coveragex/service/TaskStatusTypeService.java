package com.dilshan.coveragex.service;

import com.dilshan.coveragex.dto.TaskStatusTypeResponseDTO;
import com.dilshan.coveragex.entity.TaskStatusType;
import com.dilshan.coveragex.exception.TaskStatusTypeNotFoundException;
import com.dilshan.coveragex.repository.TaskStatusTypeRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskStatusTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusTypeService.class);

    private final TaskStatusTypeRepository taskStatusTypeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskStatusTypeService(TaskStatusTypeRepository taskStatusTypeRepository, ModelMapper modelMapper) {
        this.taskStatusTypeRepository = taskStatusTypeRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<TaskStatusTypeResponseDTO> getAllTaskStatusTypes() {
        LOGGER.info("Fetching all task status types");
        
        List<TaskStatusType> taskStatusTypes = taskStatusTypeRepository.findAll();
        
        LOGGER.info("Found {} task status types", taskStatusTypes.size());
        
        return taskStatusTypes.stream()
                .map(taskStatusType -> modelMapper.map(taskStatusType, TaskStatusTypeResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskStatusTypeResponseDTO getTaskStatusTypeById(Long id) {
        LOGGER.info("Fetching task status type with ID: {}", id);
        
        TaskStatusType taskStatusType = taskStatusTypeRepository.findById(id)
                .orElseThrow(() -> TaskStatusTypeNotFoundException.forId(id));
        
        LOGGER.info("Found task status type: {}", taskStatusType.getType());
        
        return modelMapper.map(taskStatusType, TaskStatusTypeResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public TaskStatusType getTaskStatusTypeEntityById(Long id) {
        LOGGER.debug("Fetching task status type entity with ID: {}", id);
        
        return taskStatusTypeRepository.findById(id)
                .orElseThrow(() -> TaskStatusTypeNotFoundException.forId(id));
    }

    @Transactional(readOnly = true)
    public TaskStatusTypeResponseDTO getTaskStatusTypeByType(String type) {
        LOGGER.info("Fetching task status type with type: {}", type);
        
        TaskStatusType taskStatusType = taskStatusTypeRepository.findByType(type)
                .orElseThrow(() -> new TaskStatusTypeNotFoundException("Task status type not found with type: " + type));
        
        return modelMapper.map(taskStatusType, TaskStatusTypeResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public TaskStatusType getTaskStatusTypeEntityByType(String type) {
        LOGGER.debug("Fetching task status type entity with type: {}", type);

        return taskStatusTypeRepository.findByType(type)
                .orElseThrow(() -> new TaskStatusTypeNotFoundException("Task status type not found with type: " + type));
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        LOGGER.debug("Checking if task status type exists with ID: {}", id);
        return taskStatusTypeRepository.existsById(id);
    }
}
