package xmoke;

public class Task {
    private final String description;
    private final RoutineType routineType;

    public Task(String description, RoutineType routineType) {
        this.description = description.trim();
        this.routineType = routineType;
    }

    public String getDescription() {
        return description;
    }

    public RoutineType getRoutineType() {
        return routineType;
    }

    @Override
    public String toString() {
        return description;
    }
}