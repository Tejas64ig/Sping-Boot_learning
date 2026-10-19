package com.example.taskapi.model;

/**
 * Represents the lifecycle states a Task can be in.
 *
 * Using an enum instead of a String field has two key benefits:
 *  1. Compile-time safety — you cannot accidentally set status to "DONE" vs "done" vs "Done"
 *  2. Jackson automatically converts the enum name to/from JSON strings.
 *     TaskStatus.TODO serializes to "TODO" and "TODO" deserializes back to TaskStatus.TODO.
 */
public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED
}
