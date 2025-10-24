package com.dilshan.coveragex.service.impl;

import com.dilshan.coveragex.dto.PriorityTypeResponseDTO;
import com.dilshan.coveragex.entity.PriorityType;
import com.dilshan.coveragex.exception.PriorityTypeNotFoundException;
import com.dilshan.coveragex.repository.PriorityTypeRepository;
import com.dilshan.coveragex.service.PriorityTypeService;
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
public class PriorityTypeServiceImpl implements PriorityTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriorityTypeServiceImpl.class);

    private final PriorityTypeRepository priorityTypeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PriorityTypeServiceImpl(PriorityTypeRepository priorityTypeRepository, ModelMapper modelMapper) {
        this.priorityTypeRepository = priorityTypeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriorityTypeResponseDTO> getAllPriorityTypes() {
        LOGGER.info("Fetching all priority types");
        
        List<PriorityType> priorityTypes = priorityTypeRepository.findAll();
        
        LOGGER.info("Found {} priority types", priorityTypes.size());
        
        return priorityTypes.stream()
                .map(priorityType -> modelMapper.map(priorityType, PriorityTypeResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PriorityTypeResponseDTO getPriorityTypeById(Long id) {
        LOGGER.info("Fetching priority type with ID: {}", id);
        
        PriorityType priorityType = priorityTypeRepository.findById(id)
                .orElseThrow(() -> PriorityTypeNotFoundException.forId(id));
        
        LOGGER.info("Found priority type: {}", priorityType.getType());
        
        return modelMapper.map(priorityType, PriorityTypeResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PriorityType getPriorityTypeEntityById(Long id) {
        LOGGER.debug("Fetching priority type entity with ID: {}", id);
        
        return priorityTypeRepository.findById(id)
                .orElseThrow(() -> PriorityTypeNotFoundException.forId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PriorityTypeResponseDTO getPriorityTypeByType(String type) {
        LOGGER.info("Fetching priority type with type: {}", type);
        
        PriorityType priorityType = priorityTypeRepository.findByType(type)
                .orElseThrow(() -> new PriorityTypeNotFoundException("Priority type not found with type: " + type));
        
        return modelMapper.map(priorityType, PriorityTypeResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        LOGGER.debug("Checking if priority type exists with ID: {}", id);
        return priorityTypeRepository.existsById(id);
    }
}
