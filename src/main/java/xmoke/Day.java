package xmoke;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
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
        return log == null ? "" : log;
    }

    /**
     * Updates the log text for this day.
     *
     * @param log Log text to store.
     */
    public void setLog(String log) {
        this.log = log == null ? "" : log;
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
        return dailyStatus.getOrDefault(taskName, false);
    }

    /**
     * Checks whether the specified weekly task is completed.
     *
     * @param taskName Name of the task.
     * @return True if the task is completed, otherwise false.
     */
    public boolean isWeeklyCompleted(String taskName) {
        return weeklyStatus.getOrDefault(taskName, false);
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
        for (Task task : dailyRoutines.getAllTasks()) {
            dailyStatus.putIfAbsent(task.getDescription(), false);
        }

        dailyStatus.keySet().removeIf(name -> !dailyRoutines.containsDescription(name));

        boolean sameWeekAsPrevious = previousDay != null && isSameWeek(previousDay.getDate(), this.date);

        for (Task task : weeklyRoutines.getAllTasks()) {
            String name = task.getDescription();
            if (!weeklyStatus.containsKey(name)) {
                if (sameWeekAsPrevious) {
                    weeklyStatus.put(name, previousDay.isWeeklyCompleted(name));
                } else {
                    weeklyStatus.put(name, false);
                }
            }
        }

        weeklyStatus.keySet().removeIf(name -> !weeklyRoutines.containsDescription(name));
    }

    /**
     * Checks whether two dates fall within the same week of the same year.
     *
     * @param d1 First date.
     * @param d2 Second date.
     * @return True if both dates are in the same week, otherwise false.
     */
    private boolean isSameWeek(LocalDate d1, LocalDate d2) {
        WeekFields wf = WeekFields.of(Locale.getDefault());
        return d1.getYear() == d2.getYear()
                && d1.get(wf.weekOfWeekBasedYear()) == d2.get(wf.weekOfWeekBasedYear());
    }
}
