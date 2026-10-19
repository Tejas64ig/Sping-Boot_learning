package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.model.TaskStatus;
import com.example.taskapi.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TaskController handles all HTTP requests for the /api/tasks resource.
 *
 * RESPONSIBILITIES OF THIS CLASS (and only these):
 *   - Map HTTP methods and URLs to service calls
 *   - Extract data from requests (@PathVariable, @RequestBody, @RequestParam)
 *   - Trigger validation (@Valid)
 *   - Return appropriate HTTP status codes (ResponseEntity)
 *   - Let GlobalExceptionHandler deal with errors
 *
 * WHAT THIS CLASS MUST NOT DO:
 *   - Business logic (belongs in TaskService)
 *   - Data access (belongs in a Repository, added in Project 3)
 *   - Complex data transformation (beyond constructing the response)
 *
 * ANNOTATION GUIDE:
 *
 * @RestController   — handles HTTP requests, return values written as JSON
 * @RequestMapping   — base path for all endpoints in this class
 *
 * @GetMapping       — HTTP GET
 * @PostMapping      — HTTP POST  (create a resource)
 * @PutMapping       — HTTP PUT   (replace a resource entirely)
 * @DeleteMapping    — HTTP DELETE (remove a resource)
 *
 * @PathVariable     — extracts a value from the URL path: /api/tasks/{id}
 * @RequestBody      — deserializes the JSON request body into a Java object
 * @RequestParam     — extracts a query parameter from the URL: ?status=TODO
 * @Valid            — triggers Bean Validation on the annotated parameter
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    // Constructor injection — Spring supplies the TaskService bean
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // =========================================================================
    // POST /api/tasks — Create a task
    // =========================================================================

    /**
     * Creates a new task.
     *
     * @Valid triggers validation on TaskRequest. If @NotBlank fails,
     * Spring throws MethodArgumentNotValidException BEFORE this method runs.
     * GlobalExceptionHandler catches it and returns 400 Bad Request.
     *
     * On success, we return 201 Created (not 200 OK).
     * HTTP 201 specifically means "a new resource was created as a result of this request."
     * The body contains the created resource so the client gets the assigned ID.
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =========================================================================
    // GET /api/tasks — List all tasks (optional status filter)
    // =========================================================================

    /**
     * Returns all tasks.
     *
     * @RequestParam(required = false) means the parameter is optional.
     * GET /api/tasks            → returns all tasks
     * GET /api/tasks?status=TODO → returns only TODO tasks
     *
     * Spring automatically converts the string "TODO" to TaskStatus.TODO
     * because TaskStatus is an enum. If the client sends "INVALID", Spring
     * throws a MethodArgumentTypeMismatchException (handled by our catch-all handler).
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {
        return ResponseEntity.ok(taskService.getAllTasks(status));
    }

    // =========================================================================
    // GET /api/tasks/{id} — Get one task
    // =========================================================================

    /**
     * Returns a single task by ID.
     *
     * @PathVariable binds the {id} segment from the URL to the method parameter.
     * Spring automatically converts the String "5" from the URL to Long 5L.
     *
     * If the task doesn't exist, TaskService throws TaskNotFoundException,
     * which GlobalExceptionHandler converts to 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // =========================================================================
    // PUT /api/tasks/{id} — Update a task
    // =========================================================================

    /**
     * Updates an existing task.
     *
     * PUT semantics: replace the resource at this URL with the provided data.
     * We implement a partial update (only non-null fields change) which is
     * technically closer to PATCH, but PUT is acceptable here for simplicity.
     *
     * Returns 200 OK with the updated task body.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    // =========================================================================
    // DELETE /api/tasks/{id} — Delete a task
    // =========================================================================

    /**
     * Deletes a task by ID.
     *
     * Returns 204 No Content on success.
     * HTTP 204 means "the request succeeded but there is no response body."
     * This is the correct status for DELETE — the resource no longer exists,
     * so there is nothing meaningful to return.
     *
     * Returns 404 if the task does not exist (from GlobalExceptionHandler).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
