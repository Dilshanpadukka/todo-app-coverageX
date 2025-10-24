package com.dilshan.coveragex.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public static TaskNotFoundException forId(Long taskId) {
        return new TaskNotFoundException("Task not found with ID: " + taskId);
    }
}
