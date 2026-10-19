package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.exception.GlobalExceptionHandler;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.model.TaskStatus;
import com.example.taskapi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests using @WebMvcTest and MockMvc.
 *
 * @WebMvcTest(TaskController.class):
 *   Loads ONLY the web layer — just TaskController and related MVC config.
 *   Does NOT load the full ApplicationContext (no services, no repositories).
 *   This makes it fast and focused on HTTP behavior only.
 *
 * @MockBean TaskService:
 *   Replaces the real TaskService with a Mockito mock.
 *   We define what the mock RETURNS (using when(...).thenReturn(...))
 *   so we can test the controller's behavior without any real business logic.
 *
 * MockMvc:
 *   Simulates HTTP requests and lets you assert on the response —
 *   status code, JSON body, headers — all without starting a real server.
 *
 * WHAT WE ARE TESTING HERE:
 *   - Correct HTTP status codes for each operation
 *   - JSON response structure
 *   - Validation rejection (400 Bad Request)
 *   - 404 responses for missing resources
 *
 * WHAT WE ARE NOT TESTING HERE (covered in TaskServiceTest):
 *   - Business logic inside the service
 *   - ID generation, filtering, etc.
 */
@WebMvcTest(controllers = {TaskController.class, GlobalExceptionHandler.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Jackson — converts objects to/from JSON in tests

    @MockBean
    private TaskService taskService;   // Mockito mock — no real service logic runs

    // =========================================================================
    // POST /api/tasks
    // =========================================================================

    @Test
    @DisplayName("POST /api/tasks — should return 201 with created task")
    void createTask_validRequest_returns201() throws Exception {
        TaskRequest request = buildRequest("Buy groceries", "Milk and eggs");
        TaskResponse fakeResponse = buildResponse(1L, "Buy groceries", "Milk and eggs", TaskStatus.TODO);

        // Tell the mock: "when createTask is called with any TaskRequest, return fakeResponse"
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())                    // 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("POST /api/tasks — should return 400 when title is blank")
    void createTask_blankTitle_returns400() throws Exception {
        TaskRequest badRequest = buildRequest("", "Some description"); // @NotBlank fails

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())                 // 400
                .andExpect(jsonPath("$.errors.title").exists());    // validation error field present
    }

    @Test
    @DisplayName("POST /api/tasks — should return 400 when body is missing")
    void createTask_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // GET /api/tasks
    // =========================================================================

    @Test
    @DisplayName("GET /api/tasks — should return 200 with list of tasks")
    void getAllTasks_returns200WithList() throws Exception {
        when(taskService.getAllTasks(null)).thenReturn(List.of(
                buildResponse(1L, "Task A", null, TaskStatus.TODO),
                buildResponse(2L, "Task B", null, TaskStatus.IN_PROGRESS)
        ));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task A"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    // =========================================================================
    // GET /api/tasks/{id}
    // =========================================================================

    @Test
    @DisplayName("GET /api/tasks/{id} — should return 200 with the task")
    void getTaskById_existingTask_returns200() throws Exception {
        when(taskService.getTaskById(1L))
                .thenReturn(buildResponse(1L, "Existing Task", null, TaskStatus.TODO));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Existing Task"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} — should return 404 for non-existent task")
    void getTaskById_missingTask_returns404() throws Exception {
        when(taskService.getTaskById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())                  // 404
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    // =========================================================================
    // DELETE /api/tasks/{id}
    // =========================================================================

    @Test
    @DisplayName("DELETE /api/tasks/{id} — should return 204 on success")
    void deleteTask_existingTask_returns204() throws Exception {
        // doNothing() is the default for void mock methods — no setup needed
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} — should return 404 for non-existent task")
    void deleteTask_missingTask_returns404() throws Exception {
        doThrow(new TaskNotFoundException(99L)).when(taskService).deleteTask(eq(99L));

        mockMvc.perform(delete("/api/tasks/99"))
                .andExpect(status().isNotFound()); // 404
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private TaskRequest buildRequest(String title, String description) {
        TaskRequest req = new TaskRequest();
        req.setTitle(title);
        req.setDescription(description);
        return req;
    }

    private TaskResponse buildResponse(Long id, String title, String desc, TaskStatus status) {
        TaskRequest req = new TaskRequest();
        req.setTitle(title);
        req.setDescription(desc);
        req.setStatus(status);

        // Use the static factory and a quick Task — simplest way for test data
        com.example.taskapi.model.Task task = new com.example.taskapi.model.Task(id, title, desc);
        task.setStatus(status);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return TaskResponse.from(task);
    }
}
