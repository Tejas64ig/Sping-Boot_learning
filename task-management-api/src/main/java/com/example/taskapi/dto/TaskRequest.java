package com.example.taskapi.dto;

import com.example.taskapi.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * TaskRequest is the DTO for creating or updating a task.
 *
 * It represents what the client sends in the HTTP request body.
 * Notice what is MISSING compared to the Task model:
 *   - No id       (the server generates this, the client must not supply it)
 *   - No createdAt (the server sets this on creation)
 *   - No updatedAt (the server sets this on every update)
 *
 * VALIDATION ANNOTATIONS (from jakarta.validation — Bean Validation spec):
 *
 * @NotBlank   — field must not be null AND must contain at least one non-whitespace character.
 *               "   " (spaces only) would FAIL @NotBlank but PASS @NotNull.
 *               This is usually what you want for String fields.
 *
 * @Size       — enforces min/max length on a String.
 *               message= lets you customize the error shown to the client.
 *
 * These annotations do nothing by themselves. They are activated when a
 * controller parameter is annotated with @Valid. Spring then runs validation
 * BEFORE the method body executes. If validation fails, a
 * MethodArgumentNotValidException is thrown automatically.
 * Our GlobalExceptionHandler catches that and returns a 400 Bad Request.
 */
public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Status is optional on creation (defaults to TODO in the service).
     * On update, if the client provides a status, it will be applied.
     * null means "don't change the status".
     */
    private TaskStatus status;

    // Getters and Setters

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}
