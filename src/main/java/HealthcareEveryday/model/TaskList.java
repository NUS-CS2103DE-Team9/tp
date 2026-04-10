package HealthcareEveryday.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of tasks and provides basic operations on them.
 */
public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task at the specified index.
     *
     * @param index Index of the task to remove.
     */
    public void removeTask(int index) {
        tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Index of the task.
     * @return Task at the index.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks in the list.
     *
     * @return List of tasks.
     */
    public List<Task> getAllTasks() {
        return tasks;
    }

    /**
     * Checks whether a task with the given description exists.
     *
     * @param description Task description to check.
     * @return True if exists, otherwise false.
     */
    public boolean containsDescription(String description) {
        return tasks.stream()
                .anyMatch(t -> t.getDescription().equals(description));
    }
}
