package com.dilshan.coveragex.exception;


public class TaskStatusTypeNotFoundException extends RuntimeException {

    public TaskStatusTypeNotFoundException(String message) {
        super(message);
    }

    public static TaskStatusTypeNotFoundException forId(Long statusId) {
        return new TaskStatusTypeNotFoundException("Task status type not found with ID: " + statusId);
    }
}
