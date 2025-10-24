package com.dilshan.coveragex.service;

import com.dilshan.coveragex.dto.TaskStatusTypeResponseDTO;
import com.dilshan.coveragex.entity.TaskStatusType;

import java.util.List;

public interface TaskStatusTypeService {

    List<TaskStatusTypeResponseDTO> getAllTaskStatusTypes();

    TaskStatusTypeResponseDTO getTaskStatusTypeById(Long id);

    TaskStatusType getTaskStatusTypeEntityById(Long id);

    TaskStatusTypeResponseDTO getTaskStatusTypeByType(String type);

    TaskStatusType getTaskStatusTypeEntityByType(String type);

    boolean existsById(Long id);
}
