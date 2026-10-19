package com.example.taskapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler intercepts exceptions thrown anywhere in the application
 * and converts them to clean, consistent JSON error responses.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   - @ControllerAdvice:  applies to all @RestController classes globally
 *   - @ResponseBody:      the return value is serialized to JSON (not treated as a view name)
 *
 * HOW IT WORKS:
 * When an exception propagates up through the service and controller without being caught,
 * Spring's DispatcherServlet looks for an @ExceptionHandler that matches that exception type.
 * It finds the matching method here, calls it, and uses its return value as the HTTP response.
 *
 * WITHOUT THIS CLASS:
 * Spring returns its default "Whitelabel Error Page" (HTML) or a generic JSON error
 * from BasicErrorController — neither of which is useful for an API client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // -------------------------------------------------------------------------
    // Handle 404 — Task not found
    // -------------------------------------------------------------------------

    /**
     * Handles TaskNotFoundException, which is thrown by TaskService
     * when a task ID does not exist in the in-memory store.
     *
     * Returns HTTP 404 Not Found with a descriptive JSON body.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException ex) {
        log.warn("Task not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // -------------------------------------------------------------------------
    // Handle 400 — Validation failures
    // -------------------------------------------------------------------------

    /**
     * Handles MethodArgumentNotValidException, which is thrown automatically by Spring
     * when a @Valid-annotated @RequestBody fails validation constraints.
     *
     * For example: POST /api/tasks with { "title": "" } triggers @NotBlank
     * and Spring throws this exception BEFORE our controller method runs.
     *
     * We extract each field error and include them in the response so the client
     * knows exactly which fields are invalid and why.
     *
     * Returns HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Collect all field-level validation errors into a map: { "fieldName": "error message" }
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation failed: {}", fieldErrors);

        Map<String, Object> body = errorBody(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("errors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // -------------------------------------------------------------------------
    // Handle 400 — Missing or malformed request body
    // -------------------------------------------------------------------------

    /**
     * Handles HttpMessageNotReadableException, thrown when:
     *   - The request body is completely absent (Content-Type is JSON but no body sent)
     *   - The body is malformed JSON (syntax error)
     *   - An enum field receives an unrecognised value (e.g. "status": "INVALID")
     *
     * This is DIFFERENT from MethodArgumentNotValidException:
     *   - MethodArgumentNotValidException = body was parsed OK, but field values fail @NotBlank etc.
     *   - HttpMessageNotReadableException  = body could not even be parsed / was missing entirely
     *
     * Both deserve a 400 Bad Request — the client sent an unreadable or absent payload.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        log.warn("Unreadable or missing request body: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody(HttpStatus.BAD_REQUEST,
                        "Request body is missing or malformed. Please send valid JSON."));
    }

    // -------------------------------------------------------------------------
    // Handle 500 — Unexpected errors (catch-all)
    // -------------------------------------------------------------------------

    /**
     * Catches any exception not handled by the more specific handlers above.
     *
     * IMPORTANT: We log the full stack trace here (for debugging) but we do NOT
     * include the internal exception message in the response body. Sending internal
     * error details to clients is a security risk — it leaks implementation details.
     *
     * Returns HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        log.error("Unexpected error occurred", ex); // Full stack trace in server logs
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later."));
    }

    // -------------------------------------------------------------------------
    // Helper: builds a consistent error response body
    // -------------------------------------------------------------------------

    /**
     * Creates a standardized error response map.
     * Every error response from this API has the same structure:
     * {
     *   "timestamp": "2026-10-19T19:00:00",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "Task not found with id: 99"
     * }
     *
     * Consistent error shapes make it much easier for API consumers to handle errors.
     */
    private Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
