package HealthcareEveryday.service;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.RoutineType;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.TaskList;
import HealthcareEveryday.model.User;
import HealthcareEveryday.storage.Storage;

import java.time.LocalDate;

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
        User user = storage.loadUser(userName);

        if (routineType == RoutineType.DAILY) {
            return user.getDailyRoutines();
        }

        return user.getWeeklyRoutines();
    }

    /**
     * Returns today's day record for the specified user.
     *
     * @param userName Name of the user.
     * @return Today's day record.
     */
    public Day getToday(String userName) {
        User user = storage.loadUser(userName);
        return user.getOrCreateDay(LocalDate.now());
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
        String trimmed = description == null ? "" : description.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        User user = storage.loadUser(userName);
        TaskList routines = getTaskList(user, routineType);

        for (Task task : routines.getAllTasks()) {
            if (task.getDescription().equalsIgnoreCase(trimmed)) {
                return false;
            }
        }

        routines.addTask(new Task(trimmed, routineType));
        user.getOrCreateDay(LocalDate.now());
        storage.saveUser(user);
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
        String trimmed = description == null ? "" : description.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        User user = storage.loadUser(userName);
        TaskList routines = getTaskList(user, routineType);

        for (int i = 0; i < routines.size(); i++) {
            if (routines.getTask(i).getDescription().equals(trimmed)) {
                routines.removeTask(i);
                user.getOrCreateDay(LocalDate.now());
                storage.saveUser(user);
                return true;
            }
        }

        return false;
    }

    /**
     * Updates today's completion status for a daily routine.
     *
     * @param userName Name of the user.
     * @param taskName Name of the task.
     * @param completed Completion status.
     */
    public void setDailyCompleted(String userName, String taskName, boolean completed) {
        User user = storage.loadUser(userName);
        Day today = user.getOrCreateDay(LocalDate.now());
        today.setDailyCompleted(taskName, completed);
        storage.saveUser(user);
    }

    /**
     * Updates today's completion status for a weekly routine.
     *
     * @param userName Name of the user.
     * @param taskName Name of the task.
     * @param completed Completion status.
     */
    public void setWeeklyCompleted(String userName, String taskName, boolean completed) {
        User user = storage.loadUser(userName);
        Day today = user.getOrCreateDay(LocalDate.now());
        today.setWeeklyCompleted(taskName, completed);
        storage.saveUser(user);
    }

    /**
     * Saves today's daily log for the specified user.
     *
     * @param userName Name of the user.
     * @param log Log text.
     */
    public void saveTodayLog(String userName, String log) {
        User user = storage.loadUser(userName);
        Day today = user.getOrCreateDay(LocalDate.now());
        today.setLog(log);
        storage.saveUser(user);
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
        }

        return user.getWeeklyRoutines();
    }
}