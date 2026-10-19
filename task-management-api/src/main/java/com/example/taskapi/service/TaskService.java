package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.model.Task;
import com.example.taskapi.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * TaskService contains all business logic for task management.
 *
 * DATA STORAGE:
 * We use a ConcurrentHashMap as our in-memory "database".
 * ConcurrentHashMap is thread-safe — in a web server, multiple HTTP requests
 * can arrive simultaneously from different users, so we need a map that handles
 * concurrent reads/writes without data corruption.
 *
 * AtomicLong for IDs:
 * AtomicLong.incrementAndGet() is a thread-safe way to generate sequential IDs.
 * Without atomicity, two simultaneous requests could both get ID 5.
 *
 * IMPORTANT: All data is LOST when the application restarts.
 * This is intentional — we add MySQL persistence in Project 3.
 *
 * LAYERED ARCHITECTURE:
 * The service knows nothing about HTTP. It receives DTOs (or plain data),
 * does its work, and returns DTOs. The controller handles HTTP — the service
 * handles business logic. This separation makes both easier to test independently.
 */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    // In-memory store: key = task ID, value = Task object
    private final Map<Long, Task> taskStore = new ConcurrentHashMap<>();

    // Thread-safe auto-incrementing ID generator
    private final AtomicLong idCounter = new AtomicLong(0);

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    /**
     * Creates a new task from the incoming request data.
     *
     * Notice: we never accept an ID from the client — we always generate it here.
     * This prevents clients from overwriting existing tasks by supplying an ID.
     *
     * @param request validated request DTO from the controller
     * @return response DTO with the created task including its assigned ID
     */
    public TaskResponse createTask(TaskRequest request) {
        long newId = idCounter.incrementAndGet();

        Task task = new Task(newId, request.getTitle(), request.getDescription());
        // If client supplied a status, use it; otherwise default is TODO (set in constructor)
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        taskStore.put(newId, task);
        log.info("Created task id={} title='{}'", newId, task.getTitle());

        return TaskResponse.from(task);
    }

    // -------------------------------------------------------------------------
    // READ ALL
    // -------------------------------------------------------------------------

    /**
     * Returns all tasks, optionally filtered by status.
     *
     * @param status optional filter — if null, returns all tasks
     * @return list of task response DTOs
     */
    public List<TaskResponse> getAllTasks(TaskStatus status) {
        return taskStore.values().stream()
                .filter(task -> status == null || task.getStatus() == status)
                .map(TaskResponse::from)       // method reference: same as task -> TaskResponse.from(task)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // READ ONE
    // -------------------------------------------------------------------------

    /**
     * Finds a task by ID or throws TaskNotFoundException.
     *
     * Optional<T> is Java's way of representing "a value that may or may not exist."
     * .orElseThrow() either returns the Task if present, or throws the exception.
     * This avoids null checks and makes the "not found" case explicit.
     *
     * @param id the task ID
     * @return the task response DTO
     * @throws TaskNotFoundException if no task with that ID exists
     */
    public TaskResponse getTaskById(Long id) {
        Task task = findTaskOrThrow(id);
        return TaskResponse.from(task);
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Updates an existing task.
     *
     * We apply a PARTIAL update strategy: only non-null fields in the request
     * replace the existing values. This means a client can update just the title
     * without touching the description or status.
     *
     * Note: updatedAt is always refreshed on any update call.
     *
     * @param id      the task to update
     * @param request the update data (null fields are ignored)
     * @return the updated task response DTO
     * @throws TaskNotFoundException if no task with that ID exists
     */
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = findTaskOrThrow(id);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskStore.put(id, task); // update the map entry

        log.info("Updated task id={}", id);
        return TaskResponse.from(task);
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    /**
     * Deletes a task by ID.
     *
     * We verify the task exists first (throws 404 if not found)
     * so that DELETE on a non-existent resource returns 404, not 204.
     * This is the correct RESTful behavior.
     *
     * @param id the task to delete
     * @throws TaskNotFoundException if no task with that ID exists
     */
    public void deleteTask(Long id) {
        findTaskOrThrow(id); // throws 404 if not found
        taskStore.remove(id);
        log.info("Deleted task id={}", id);
    }

    // -------------------------------------------------------------------------
    // Private helper
    // -------------------------------------------------------------------------

    /**
     * Looks up a task by ID. If not found, throws TaskNotFoundException.
     * Centralizing this logic avoids duplicating the null-check in every method.
     */
    private Task findTaskOrThrow(Long id) {
        return Optional.ofNullable(taskStore.get(id))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
