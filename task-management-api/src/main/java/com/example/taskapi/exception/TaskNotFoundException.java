package com.example.taskapi.exception;

/**
 * Thrown when a task with a given ID does not exist.
 *
 * By extending RuntimeException, we make it an "unchecked" exception.
 * This means callers (the service, controller) don't need to declare
 * "throws TaskNotFoundException" — Spring will catch it in the GlobalExceptionHandler.
 *
 * The message is used in the error response body sent to the client.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task not found with id: " + id);
    }
}
