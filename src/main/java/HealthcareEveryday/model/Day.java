package HealthcareEveryday.model;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a user's record for a single day, including log entries and
 * completion status for daily and weekly tasks.
 */
public class Day {
    private final LocalDate date;
    private String log;
    private final LinkedHashMap<String, Boolean> dailyStatus;
    private final LinkedHashMap<String, Boolean> weeklyStatus;

    /**
     * Creates a day record for the given date.
     *
     * @param date Date represented by this record.
     */
    public Day(LocalDate date) {
        this.date = date;
        this.log = "";
        this.dailyStatus = new LinkedHashMap<>();
        this.weeklyStatus = new LinkedHashMap<>();
    }

    /**
     * Returns the date of this record.
     *
     * @return Date of this record.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the stored log text for this day.
     *
     * @return Log text, or an empty string if no log is stored.
     */
    public String getLog() {
        if (log == null) {
            return "";
        } else {
            return log;
        }
    }

    /**
     * Updates the log text for this day.
     *
     * @param log Log text to store.
     */
    public void setLog(String log) {
        if (log == null) {
            this.log = "";
        } else {
            this.log = log;
        }
    }

    /**
     * Returns the completion status map for daily tasks.
     *
     * @return Map of daily task names to completion status.
     */
    public Map<String, Boolean> getDailyStatus() {
        return dailyStatus;
    }

    /**
     * Returns the completion status map for weekly tasks.
     *
     * @return Map of weekly task names to completion status.
     */
    public Map<String, Boolean> getWeeklyStatus() {
        return weeklyStatus;
    }

    /**
     * Checks whether the specified daily task is completed.
     *
     * @param taskName Name of the task.
     * @return True if the task is completed, otherwise false.
     */
    public boolean isDailyCompleted(String taskName) {
        Boolean completed = dailyStatus.get(taskName);

        if (completed == null) {
            return false;
        } else {
            return completed;
        }
    }

    /**
     * Checks whether the specified weekly task is completed.
     *
     * @param taskName Name of the task.
     * @return True if the task is completed, otherwise false.
     */
    public boolean isWeeklyCompleted(String taskName) {
        Boolean completed = weeklyStatus.get(taskName);

        if (completed == null) {
            return false;
        } else {
            return completed;
        }
    }

    /**
     * Updates the completion status of a daily task.
     *
     * @param taskName Name of the task.
     * @param completed Completion status to store.
     */
    public void setDailyCompleted(String taskName, boolean completed) {
        dailyStatus.put(taskName, completed);
    }

    /**
     * Updates the completion status of a weekly task.
     *
     * @param taskName Name of the task.
     * @param completed Completion status to store.
     */
    public void setWeeklyCompleted(String taskName, boolean completed) {
        weeklyStatus.put(taskName, completed);
    }

    /**
     * Synchronizes stored task status with the current daily and weekly routines.
     *
     * @param dailyRoutines Current list of daily routines.
     * @param weeklyRoutines Current list of weekly routines.
     * @param previousDay Previous day record, if available.
     */
    public void syncWithRoutines(TaskList dailyRoutines, TaskList weeklyRoutines, Day previousDay) {
        syncDailyRoutines(dailyRoutines);
        removeDeletedDailyRoutines(dailyRoutines);

        boolean sameWeekAsPrevious = shouldCarryForwardWeeklyStatus(previousDay);

        syncWeeklyRoutines(weeklyRoutines, previousDay, sameWeekAsPrevious);
        removeDeletedWeeklyRoutines(weeklyRoutines);
    }

    /**
     * Adds any missing daily routines to the daily status map.
     *
     * @param dailyRoutines Current daily routine list.
     */
    private void syncDailyRoutines(TaskList dailyRoutines) {
        for (Task task : dailyRoutines.getAllTasks()) {
            String taskName = task.getDescription();

            if (!dailyStatus.containsKey(taskName)) {
                dailyStatus.put(taskName, false);
            }
        }
    }

    /**
     * Removes daily status entries for tasks that no longer exist.
     *
     * @param dailyRoutines Current daily routine list.
     */
    private void removeDeletedDailyRoutines(TaskList dailyRoutines) {
        Iterator<String> iterator = dailyStatus.keySet().iterator();

        while (iterator.hasNext()) {
            String taskName = iterator.next();

            if (!dailyRoutines.containsDescription(taskName)) {
                iterator.remove();
            }
        }
    }

    /**
     * Returns whether weekly task completion should be carried forward
     * from the previous day.
     *
     * @param previousDay Previous day record, if available.
     * @return True if previous weekly status should be reused.
     */
    private boolean shouldCarryForwardWeeklyStatus(Day previousDay) {
        if (previousDay == null) {
            return false;
        }

        return isSameWeek(previousDay.getDate(), this.date);
    }

    /**
     * Adds any missing weekly routines to the weekly status map.
     *
     * @param weeklyRoutines Current weekly routine list.
     * @param previousDay Previous day record, if available.
     * @param sameWeekAsPrevious True if the previous day is in the same week.
     */
    private void syncWeeklyRoutines(TaskList weeklyRoutines, Day previousDay, boolean sameWeekAsPrevious) {
        for (Task task : weeklyRoutines.getAllTasks()) {
            String taskName = task.getDescription();

            if (!weeklyStatus.containsKey(taskName)) {
                boolean completed = getInitialWeeklyCompletion(taskName, previousDay, sameWeekAsPrevious);
                weeklyStatus.put(taskName, completed);
            }
        }
    }

    /**
     * Returns the initial weekly completion status for a newly added weekly task.
     *
     * @param taskName Name of the task.
     * @param previousDay Previous day record, if available.
     * @param sameWeekAsPrevious True if the previous day is in the same week.
     * @return Initial completion status.
     */
    private boolean getInitialWeeklyCompletion(String taskName, Day previousDay, boolean sameWeekAsPrevious) {
        if (!sameWeekAsPrevious) {
            return false;
        }

        return previousDay.isWeeklyCompleted(taskName);
    }

    /**
     * Removes weekly status entries for tasks that no longer exist.
     *
     * @param weeklyRoutines Current weekly routine list.
     */
    private void removeDeletedWeeklyRoutines(TaskList weeklyRoutines) {
        Iterator<String> iterator = weeklyStatus.keySet().iterator();

        while (iterator.hasNext()) {
            String taskName = iterator.next();

            if (!weeklyRoutines.containsDescription(taskName)) {
                iterator.remove();
            }
        }
    }

    /**
     * Checks whether two dates fall within the same week of the same year.
     *
     * @param d1 First date.
     * @param d2 Second date.
     * @return True if both dates are in the same week, otherwise false.
     */
    private boolean isSameWeek(LocalDate d1, LocalDate d2) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int firstWeek = d1.get(weekFields.weekOfWeekBasedYear());
        int secondWeek = d2.get(weekFields.weekOfWeekBasedYear());

        if (d1.getYear() != d2.getYear()) {
            return false;
        }

        return firstWeek == secondWeek;
    }
}
