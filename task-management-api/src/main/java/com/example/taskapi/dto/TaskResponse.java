package com.example.taskapi.dto;

import com.example.taskapi.model.Task;
import com.example.taskapi.model.TaskStatus;

import java.time.LocalDateTime;

/**
 * TaskResponse is the DTO for what we send back to the client.
 *
 * It is constructed from a Task model object inside the service or controller.
 * This gives us full control over what the client sees:
 *   - We can omit internal fields (e.g., internal audit fields)
 *   - We can rename fields for the API without changing the model
 *   - We can add computed fields that don't exist in the model
 *
 * The static factory method from(Task task) is a clean pattern for
 * converting a model object into a response DTO without polluting the
 * Task model with API concerns.
 */
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Static factory method — converts a Task model to a TaskResponse
    // -------------------------------------------------------------------------

    /**
     * Creates a TaskResponse from a Task domain object.
     *
     * Using a static factory method keeps the mapping logic in one place.
     * If the Task model changes, you only update this one method.
     *
     * @param task the internal Task model
     * @return a TaskResponse safe to return to the client
     */
    public static TaskResponse from(Task task) {
        TaskResponse response = new TaskResponse();
        response.id = task.getId();
        response.title = task.getTitle();
        response.description = task.getDescription();
        response.status = task.getStatus();
        response.createdAt = task.getCreatedAt();
        response.updatedAt = task.getUpdatedAt();
        return response;
    }

    // Getters (no setters needed — response objects should be read-only)

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
