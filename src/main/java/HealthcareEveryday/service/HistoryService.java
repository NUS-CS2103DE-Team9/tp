package HealthcareEveryday.service;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.User;
import HealthcareEveryday.storage.Storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles history-related logic.
 */
public class HistoryService {
    private final Storage storage;

    /**
     * Creates a history service with the given storage.
     *
     * @param storage Storage instance.
     */
    public HistoryService(Storage storage) {
        this.storage = storage;
    }

    /**
     * Returns today's history for all users.
     *
     * @return List of today's user history entries.
     */
    public List<TodayUserHistory> getTodayHistoryForAllUsers() {
        List<TodayUserHistory> result = new ArrayList<>();

        for (String userName : storage.listSeniorNames()) {
            User user = storage.loadUser(userName);
            Day today = user.getOrCreateDay(LocalDate.now());

            List<TaskStatus> dailyStatuses = new ArrayList<>();
            for (Task task : user.getDailyRoutines().getAllTasks()) {
                dailyStatuses.add(new TaskStatus(
                        task.getDescription(),
                        today.isDailyCompleted(task.getDescription())
                ));
            }

            List<TaskStatus> weeklyStatuses = new ArrayList<>();
            for (Task task : user.getWeeklyRoutines().getAllTasks()) {
                weeklyStatuses.add(new TaskStatus(
                        task.getDescription(),
                        today.isWeeklyCompleted(task.getDescription())
                ));
            }

            result.add(new TodayUserHistory(userName, dailyStatuses, weeklyStatuses));
            storage.saveUser(user);
        }

        return result;
    }

    /**
     * Returns weekly history for the specified user.
     *
     * @param userName Name of the user.
     * @return Weekly history data.
     */
    public WeeklyUserHistory getWeeklyHistory(String userName) {
        User user = storage.loadUser(userName);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<DailyTaskWeeklyRecord> dailyRecords = new ArrayList<>();
        for (Task task : user.getDailyRoutines().getAllTasks()) {
            int completedCount = 0;
            List<String> missedDays = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                Day day = user.getDay(date);

                boolean done = day != null && day.isDailyCompleted(task.getDescription());
                if (done) {
                    completedCount++;
                } else {
                    missedDays.add(formatDayName(date));
                }
            }

            dailyRecords.add(new DailyTaskWeeklyRecord(
                    task.getDescription(),
                    completedCount,
                    missedDays
            ));
        }

        List<WeeklyTaskWeeklyRecord> weeklyRecords = new ArrayList<>();
        for (Task task : user.getWeeklyRoutines().getAllTasks()) {
            boolean done = false;
            List<String> doneDays = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                Day day = user.getDay(date);

                boolean marked = day != null && day.isWeeklyCompleted(task.getDescription());
                if (marked) {
                    done = true;
                    doneDays.add(formatDayName(date));
                }
            }

            weeklyRecords.add(new WeeklyTaskWeeklyRecord(
                    task.getDescription(),
                    done,
                    doneDays
            ));
        }

        storage.saveUser(user);
        return new WeeklyUserHistory(userName, startDate, endDate, dailyRecords, weeklyRecords);
    }

    /**
     * Formats day name with leading capital letter.
     *
     * @param date Date to format.
     * @return Formatted day name.
     */
    private String formatDayName(LocalDate date) {
        String raw = date.getDayOfWeek().toString().toLowerCase();
        return raw.substring(0, 1).toUpperCase() + raw.substring(1);
    }

    /**
     * Represents a task completion status.
     */
    public static class TaskStatus {
        private final String taskName;
        private final boolean completed;

        public TaskStatus(String taskName, boolean completed) {
            this.taskName = taskName;
            this.completed = completed;
        }

        public String getTaskName() {
            return taskName;
        }

        public boolean isCompleted() {
            return completed;
        }
    }

    /**
     * Represents today's history for one user.
     */
    public static class TodayUserHistory {
        private final String userName;
        private final List<TaskStatus> dailyTasks;
        private final List<TaskStatus> weeklyTasks;

        public TodayUserHistory(String userName, List<TaskStatus> dailyTasks, List<TaskStatus> weeklyTasks) {
            this.userName = userName;
            this.dailyTasks = dailyTasks;
            this.weeklyTasks = weeklyTasks;
        }

        public String getUserName() {
            return userName;
        }

        public List<TaskStatus> getDailyTasks() {
            return dailyTasks;
        }

        public List<TaskStatus> getWeeklyTasks() {
            return weeklyTasks;
        }
    }

    /**
     * Represents weekly history for one daily task.
     */
    public static class DailyTaskWeeklyRecord {
        private final String taskName;
        private final int completedCount;
        private final List<String> missedDays;

        public DailyTaskWeeklyRecord(String taskName, int completedCount, List<String> missedDays) {
            this.taskName = taskName;
            this.completedCount = completedCount;
            this.missedDays = missedDays;
        }

        public String getTaskName() {
            return taskName;
        }

        public int getCompletedCount() {
            return completedCount;
        }

        public List<String> getMissedDays() {
            return missedDays;
        }
    }

    /**
     * Represents weekly history for one weekly task.
     */
    public static class WeeklyTaskWeeklyRecord {
        private final String taskName;
        private final boolean doneThisWeek;
        private final List<String> doneDays;

        public WeeklyTaskWeeklyRecord(String taskName, boolean doneThisWeek, List<String> doneDays) {
            this.taskName = taskName;
            this.doneThisWeek = doneThisWeek;
            this.doneDays = doneDays;
        }

        public String getTaskName() {
            return taskName;
        }

        public boolean isDoneThisWeek() {
            return doneThisWeek;
        }

        public List<String> getDoneDays() {
            return doneDays;
        }
    }

    /**
     * Represents the full weekly history for one user.
     */
    public static class WeeklyUserHistory {
        private final String userName;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final List<DailyTaskWeeklyRecord> dailyRecords;
        private final List<WeeklyTaskWeeklyRecord> weeklyRecords;

        public WeeklyUserHistory(
                String userName,
                LocalDate startDate,
                LocalDate endDate,
                List<DailyTaskWeeklyRecord> dailyRecords,
                List<WeeklyTaskWeeklyRecord> weeklyRecords
        ) {
            this.userName = userName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.dailyRecords = dailyRecords;
            this.weeklyRecords = weeklyRecords;
        }

        public String getUserName() {
            return userName;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public List<DailyTaskWeeklyRecord> getDailyRecords() {
            return dailyRecords;
        }

        public List<WeeklyTaskWeeklyRecord> getWeeklyRecords() {
            return weeklyRecords;
        }
    }
}
