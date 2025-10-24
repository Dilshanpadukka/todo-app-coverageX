package com.dilshan.coveragex.controller;

import com.dilshan.coveragex.dto.PriorityTypeResponseDTO;
import com.dilshan.coveragex.service.PriorityTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/priority-types")
@CrossOrigin(origins = {"http://localhost:3000"})
public class PriorityTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriorityTypeController.class);
    private final PriorityTypeService priorityTypeService;
    @Autowired
    public PriorityTypeController(PriorityTypeService priorityTypeService) {
        this.priorityTypeService = priorityTypeService;
    }

    @GetMapping
    public ResponseEntity<List<PriorityTypeResponseDTO>> getAllPriorityTypes() {
        List<PriorityTypeResponseDTO> priorityTypes = priorityTypeService.getAllPriorityTypes();
        LOGGER.info("Successfully retrieved {} priority types", priorityTypes.size());
        return ResponseEntity.ok(priorityTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriorityTypeResponseDTO> getPriorityTypeById(
            @Parameter(description = "Priority type ID", required = true, example = "1")
            @PathVariable Long id) {
        PriorityTypeResponseDTO priorityType = priorityTypeService.getPriorityTypeById(id);
        LOGGER.info("Successfully retrieved priority type: {}", priorityType.getType());
        return ResponseEntity.ok(priorityType);
    }
}
