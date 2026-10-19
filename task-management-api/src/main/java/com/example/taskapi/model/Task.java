package com.example.taskapi.model;

import java.time.LocalDateTime;

/**
 * Task is our core domain model — it represents a task in the system.
 *
 * This is NOT a JPA entity (no @Entity, no database). In Project 2, we store
 * tasks in a plain in-memory HashMap inside TaskService. The point of this
 * project is to understand the HTTP layer and architecture — not persistence.
 * We add the database in Project 3.
 *
 * WHY NOT expose this class directly from the controller?
 * We use DTOs (TaskRequest, TaskResponse) instead of exposing Task directly:
 *  - TaskRequest: what the client sends IN (only the fields the client should control)
 *  - TaskResponse: what we send OUT (only the fields the client should see)
 *  - Task: our internal model with all fields (could contain sensitive or internal data)
 *
 * This separation protects you from "mass assignment" vulnerabilities and lets you
 * evolve your internal model without breaking the public API.
 */
public class Task {

    private Long id;
    private String title;
    private String description;

    /**
     * Status defaults to TODO when a task is created.
     * This is a business rule, not just a default value.
     */
    private TaskStatus status = TaskStatus.TODO;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Task() {
    }

    public Task(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // Plain getters/setters — no Lombok in this project intentionally.
    // Writing them manually helps you understand what Lombok generates later.
    // -------------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
