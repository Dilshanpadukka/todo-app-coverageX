package com.dilshan.coveragex.service;

import com.dilshan.coveragex.dto.PriorityTypeResponseDTO;
import com.dilshan.coveragex.entity.PriorityType;

import java.util.List;

public interface PriorityTypeService {

    List<PriorityTypeResponseDTO> getAllPriorityTypes();

    PriorityTypeResponseDTO getPriorityTypeById(Long id);

    PriorityType getPriorityTypeEntityById(Long id);

    PriorityTypeResponseDTO getPriorityTypeByType(String type);

    boolean existsById(Long id);
}
