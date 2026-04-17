package HealthcareEveryday.service;

import java.time.LocalDate;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.RoutineType;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.TaskList;
import HealthcareEveryday.model.User;
import HealthcareEveryday.storage.Storage;

/**
 * Handles routine-related application logic.
 */
public class RoutineService {
    private final Storage storage;

    /**
     * Creates a routine service with the given storage.
     *
     * @param storage Storage instance.
     */
    public RoutineService(Storage storage) {
        this.storage = storage;
    }

    /**
     * Loads and returns the user with the given name.
     *
     * @param userName Name of the user.
     * @return Loaded user.
     */
    public User getUser(String userName) {
        return storage.loadUser(userName);
    }

    /**
     * Returns the routines of the given type for the specified user.
     *
     * @param userName Name of the user.
     * @param routineType Type of routine.
     * @return TaskList of the requested routine type.
     */
    public TaskList getRoutines(String userName, RoutineType routineType) {
        User user = loadUser(userName);
        return getTaskList(user, routineType);
    }

    /**
     * Returns today's day record for the specified user.
     *
     * @param userName Name of the user.
     * @return Today's day record.
     */
    public Day getToday(String userName) {
        User user = loadUser(userName);
        return getTodayDay(user);
    }

    /**
     * Adds a routine for the specified user.
     *
     * @param userName Name of the user.
     * @param description Description of the routine.
     * @param routineType Type of routine.
     * @return True if added successfully, otherwise false.
     */
    public boolean addRoutine(String userName, String description, RoutineType routineType) {
        String trimmedDescription = normalizeDescription(description);

        if (trimmedDescription.isEmpty()) {
            return false;
        }

        User user = loadUser(userName);
        TaskList routines = getTaskList(user, routineType);

        if (containsTaskDescription(routines, trimmedDescription)) {
            return false;
        }

        Task newTask = new Task(trimmedDescription, routineType);
        routines.addTask(newTask);

        ensureTodayExists(user);
        saveUser(user);
        return true;
    }

    /**
     * Removes a routine for the specified user.
     *
     * @param userName Name of the user.
     * @param description Description of the routine.
     * @param routineType Type of routine.
     * @return True if removed successfully, otherwise false.
     */
    public boolean removeRoutine(String userName, String description, RoutineType routineType) {
        String trimmedDescription = normalizeDescription(description);

        if (trimmedDescription.isEmpty()) {
            return false;
        }

        User user = loadUser(userName);
        TaskList routines = getTaskList(user, routineType);
        int taskIndex = findTaskIndex(routines, trimmedDescription);

        if (taskIndex == -1) {
            return false;
        }

        routines.removeTask(taskIndex);
        ensureTodayExists(user);
        saveUser(user);
        return true;
    }

    /**
     * Updates today's completion status for a daily routine.
     *
     * @param userName Name of the user.
     * @param taskName Name of the task.
     * @param completed Completion status.
     */
    public void setDailyCompleted(String userName, String taskName, boolean completed) {
        User user = loadUser(userName);
        Day today = getTodayDay(user);
        today.setDailyCompleted(taskName, completed);
        saveUser(user);
    }

    /**
     * Updates today's completion status for a weekly routine.
     *
     * @param userName Name of the user.
     * @param taskName Name of the task.
     * @param completed Completion status.
     */
    public void setWeeklyCompleted(String userName, String taskName, boolean completed) {
        User user = loadUser(userName);
        Day today = getTodayDay(user);
        today.setWeeklyCompleted(taskName, completed);
        saveUser(user);
    }

    /**
     * Saves today's daily log for the specified user.
     *
     * @param userName Name of the user.
     * @param log Log text.
     */
    public void saveTodayLog(String userName, String log) {
        User user = loadUser(userName);
        Day today = getTodayDay(user);
        today.setLog(log);
        saveUser(user);
    }

    /**
     * Loads the user from storage.
     *
     * @param userName Name of the user.
     * @return Loaded user.
     */
    private User loadUser(String userName) {
        return storage.loadUser(userName);
    }

    /**
     * Saves the user to storage.
     *
     * @param user User to save.
     */
    private void saveUser(User user) {
        storage.saveUser(user);
    }

    /**
     * Returns today's day record for the given user.
     *
     * @param user User to read from.
     * @return Today's day record.
     */
    private Day getTodayDay(User user) {
        return user.getOrCreateDay(LocalDate.now());
    }

    /**
     * Ensures today's day record exists for the user.
     *
     * @param user User to update.
     */
    private void ensureTodayExists(User user) {
        user.getOrCreateDay(LocalDate.now());
    }

    /**
     * Returns the correct routine list from the user.
     *
     * @param user User to read from.
     * @param routineType Type of routine.
     * @return Matching task list.
     */
    private TaskList getTaskList(User user, RoutineType routineType) {
        if (routineType == RoutineType.DAILY) {
            return user.getDailyRoutines();
        } else {
            return user.getWeeklyRoutines();
        }
    }

    /**
     * Normalizes a routine description.
     *
     * @param description Raw routine description.
     * @return Trimmed description, or an empty string if null.
     */
    private String normalizeDescription(String description) {
        if (description == null) {
            return "";
        }

        return description.trim();
    }

    /**
     * Returns whether the given task list contains a task with the description.
     *
     * @param routines Task list to search.
     * @param description Description to match.
     * @return True if found, otherwise false.
     */
    private boolean containsTaskDescription(TaskList routines, String description) {
        for (Task task : routines.getAllTasks()) {
            if (task.getDescription().equalsIgnoreCase(description)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds the index of a task with the given description.
     *
     * @param routines Task list to search.
     * @param description Description to match.
     * @return Matching index, or -1 if not found.
     */
    private int findTaskIndex(TaskList routines, String description) {
        for (int i = 0; i < routines.size(); i++) {
            String taskDescription = routines.getTask(i).getDescription();

            if (taskDescription.equals(description)) {
                return i;
            }
        }

        return -1;
    }
}
