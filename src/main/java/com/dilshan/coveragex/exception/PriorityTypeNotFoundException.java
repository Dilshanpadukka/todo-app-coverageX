package com.dilshan.coveragex.exception;


public class PriorityTypeNotFoundException extends RuntimeException {

    public PriorityTypeNotFoundException(String message) {
        super(message);
    }

    public static PriorityTypeNotFoundException forId(Long priorityId) {
        return new PriorityTypeNotFoundException("Priority type not found with ID: " + priorityId);
    }
}
