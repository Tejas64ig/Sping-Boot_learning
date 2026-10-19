package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TaskService.
 *
 * WHAT "UNIT TEST" MEANS HERE:
 * We test TaskService IN ISOLATION. No Spring context, no HTTP, no database.
 * We just create a TaskService object with `new` and call its methods directly.
 * This is fast (milliseconds) and pinpoints exactly what the service does.
 *
 * @SpringBootTest is NOT used here — that loads the full Spring context and is
 * much slower. Save @SpringBootTest for integration tests.
 *
 * AssertJ (assertThat) is used instead of JUnit's assertEquals because
 * AssertJ produces much more readable error messages when tests fail.
 * Example:
 *   JUnit:   expected <5> but was <3>
 *   AssertJ: Expected size: 5 but was: 3 in: [...]
 */
class TaskServiceTest {

    private TaskService taskService;

    /**
     * @BeforeEach runs before EVERY test method.
     * We create a fresh TaskService before each test so tests are completely
     * independent — one test's created tasks don't affect another test.
     */
    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    // =========================================================================
    // CREATE tests
    // =========================================================================

    @Test
    @DisplayName("Should create a task and return it with an assigned ID")
    void createTask_shouldReturnTaskWithId() {
        TaskRequest request = buildRequest("Buy groceries", "Milk, eggs, bread", null);

        TaskResponse response = taskService.createTask(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getId()).isGreaterThan(0);
        assertThat(response.getTitle()).isEqualTo("Buy groceries");
        assertThat(response.getDescription()).isEqualTo("Milk, eggs, bread");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO); // default status
    }

    @Test
    @DisplayName("Should assign sequential IDs to created tasks")
    void createTask_shouldAssignSequentialIds() {
        TaskResponse first  = taskService.createTask(buildRequest("Task A", null, null));
        TaskResponse second = taskService.createTask(buildRequest("Task B", null, null));
        TaskResponse third  = taskService.createTask(buildRequest("Task C", null, null));

        assertThat(second.getId()).isGreaterThan(first.getId());
        assertThat(third.getId()).isGreaterThan(second.getId());
    }

    @Test
    @DisplayName("Should use provided status if given on creation")
    void createTask_shouldUseProvidedStatus() {
        TaskRequest request = buildRequest("Write report", null, TaskStatus.IN_PROGRESS);

        TaskResponse response = taskService.createTask(request);

        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    // =========================================================================
    // READ ALL tests
    // =========================================================================

    @Test
    @DisplayName("Should return all tasks when no status filter is applied")
    void getAllTasks_noFilter_shouldReturnAll() {
        taskService.createTask(buildRequest("Task 1", null, null));
        taskService.createTask(buildRequest("Task 2", null, TaskStatus.IN_PROGRESS));

        List<TaskResponse> tasks = taskService.getAllTasks(null);

        assertThat(tasks).hasSize(2);
    }

    @Test
    @DisplayName("Should return only tasks matching the status filter")
    void getAllTasks_withStatusFilter_shouldReturnFiltered() {
        taskService.createTask(buildRequest("Task A", null, null));                         // TODO
        taskService.createTask(buildRequest("Task B", null, TaskStatus.IN_PROGRESS));
        taskService.createTask(buildRequest("Task C", null, TaskStatus.IN_PROGRESS));

        List<TaskResponse> inProgress = taskService.getAllTasks(TaskStatus.IN_PROGRESS);

        assertThat(inProgress).hasSize(2);
        assertThat(inProgress).allMatch(t -> t.getStatus() == TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void getAllTasks_whenEmpty_shouldReturnEmptyList() {
        List<TaskResponse> tasks = taskService.getAllTasks(null);
        assertThat(tasks).isEmpty();
    }

    // =========================================================================
    // READ ONE tests
    // =========================================================================

    @Test
    @DisplayName("Should return a task by its ID")
    void getTaskById_existingId_shouldReturnTask() {
        TaskResponse created = taskService.createTask(buildRequest("Existing Task", null, null));

        TaskResponse found = taskService.getTaskById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getTitle()).isEqualTo("Existing Task");
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException for a non-existent ID")
    void getTaskById_missingId_shouldThrowException() {
        // assertThatThrownBy verifies that the code inside the lambda throws an exception
        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    // =========================================================================
    // UPDATE tests
    // =========================================================================

    @Test
    @DisplayName("Should update task title and status")
    void updateTask_shouldApplyChanges() {
        TaskResponse created = taskService.createTask(buildRequest("Original title", null, null));

        TaskRequest updateRequest = buildRequest("Updated title", "New description", TaskStatus.IN_PROGRESS);
        TaskResponse updated = taskService.updateTask(created.getId(), updateRequest);

        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getDescription()).isEqualTo("New description");
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when updating non-existent task")
    void updateTask_missingId_shouldThrowException() {
        assertThatThrownBy(() -> taskService.updateTask(999L, buildRequest("X", null, null)))
                .isInstanceOf(TaskNotFoundException.class);
    }

    // =========================================================================
    // DELETE tests
    // =========================================================================

    @Test
    @DisplayName("Should delete a task and make it unretrievable")
    void deleteTask_shouldRemoveTask() {
        TaskResponse created = taskService.createTask(buildRequest("To be deleted", null, null));

        taskService.deleteTask(created.getId());

        // After deletion, getting it by ID should throw
        assertThatThrownBy(() -> taskService.getTaskById(created.getId()))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when deleting non-existent task")
    void deleteTask_missingId_shouldThrowException() {
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private TaskRequest buildRequest(String title, String description, TaskStatus status) {
        TaskRequest req = new TaskRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setStatus(status);
        return req;
    }
}
