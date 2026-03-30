package xmoke;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Day {
    private final LocalDate date;
    private String log;
    private final LinkedHashMap<String, Boolean> dailyStatus;
    private final LinkedHashMap<String, Boolean> weeklyStatus;

    public Day(LocalDate date) {
        this.date = date;
        this.log = "";
        this.dailyStatus = new LinkedHashMap<>();
        this.weeklyStatus = new LinkedHashMap<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLog() {
        return log == null ? "" : log;
    }

    public void setLog(String log) {
        this.log = log == null ? "" : log;
    }

    public Map<String, Boolean> getDailyStatus() {
        return dailyStatus;
    }

    public Map<String, Boolean> getWeeklyStatus() {
        return weeklyStatus;
    }

    public boolean isDailyCompleted(String taskName) {
        return dailyStatus.getOrDefault(taskName, false);
    }

    public boolean isWeeklyCompleted(String taskName) {
        return weeklyStatus.getOrDefault(taskName, false);
    }

    public void setDailyCompleted(String taskName, boolean completed) {
        dailyStatus.put(taskName, completed);
    }

    public void setWeeklyCompleted(String taskName, boolean completed) {
        weeklyStatus.put(taskName, completed);
    }

    public void syncWithRoutines(TaskList dailyRoutines, TaskList weeklyRoutines, Day previousDay) {
        // Daily tasks always reset to false for a new day if not already present
        for (Task task : dailyRoutines.getAllTasks()) {
            dailyStatus.putIfAbsent(task.getDescription(), false);
        }

        // Remove statuses for deleted daily tasks
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

        // Remove statuses for deleted weekly tasks
        weeklyStatus.keySet().removeIf(name -> !weeklyRoutines.containsDescription(name));
    }

    private boolean isSameWeek(LocalDate d1, LocalDate d2) {
        WeekFields wf = WeekFields.of(Locale.getDefault());
        return d1.getYear() == d2.getYear()
                && d1.get(wf.weekOfWeekBasedYear()) == d2.get(wf.weekOfWeekBasedYear());
    }
}
