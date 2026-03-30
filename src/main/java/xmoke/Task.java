package xmoke;

/**
 * Represents a routine task with a description and routine type.
 */
public class Task {
    private final String description;
    private final RoutineType routineType;

    /**
     * Creates a task with the given description and routine type.
     *
     * @param description Description of the task.
     * @param routineType Type of routine this task belongs to.
     */
    public Task(String description, RoutineType routineType) {
        this.description = description.trim();
        this.routineType = routineType;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the routine type of the task.
     *
     * @return Routine type of the task.
     */
    public RoutineType getRoutineType() {
        return routineType;
    }

    /**
     * Returns the string representation of the task.
     *
     * @return Task description.
     */
    @Override
    public String toString() {
        return description;
    }
}
