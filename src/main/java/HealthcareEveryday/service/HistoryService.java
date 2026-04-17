package HealthcareEveryday.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.TaskList;
import HealthcareEveryday.model.User;
import HealthcareEveryday.storage.Storage;

/**
 * Handles history-related logic.
 */
public class HistoryService {
    private static final int WEEK_LENGTH_DAYS = 7;

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
        List<String> userNames = storage.listSeniorNames();

        for (String userName : userNames) {
            TodayUserHistory userHistory = buildTodayUserHistory(userName);
            result.add(userHistory);
        }

        return result;
    }

    /**
     * Builds today's history for one user.
     *
     * @param userName Name of the user.
     * @return Today's history for that user.
     */
    private TodayUserHistory buildTodayUserHistory(String userName) {
        User user = storage.loadUser(userName);
        Day today = user.getOrCreateDay(LocalDate.now());

        List<TaskStatus> dailyStatuses = buildTodayTaskStatuses(
                user.getDailyRoutines(),
                today,
                true
        );
        List<TaskStatus> weeklyStatuses = buildTodayTaskStatuses(
                user.getWeeklyRoutines(),
                today,
                false
        );

        storage.saveUser(user);
        return new TodayUserHistory(userName, dailyStatuses, weeklyStatuses);
    }

    /**
     * Builds today's task status list for either daily or weekly routines.
     *
     * @param tasks Task list to read from.
     * @param day Day record to check.
     * @param isDaily True for daily routines, false for weekly routines.
     * @return List of task statuses.
     */
    private List<TaskStatus> buildTodayTaskStatuses(TaskList tasks, Day day, boolean isDaily) {
        List<TaskStatus> statuses = new ArrayList<>();

        for (Task task : tasks.getAllTasks()) {
            boolean completed = isTaskCompleted(day, task.getDescription(), isDaily);
            TaskStatus status = new TaskStatus(task.getDescription(), completed);
            statuses.add(status);
        }

        return statuses;
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
        LocalDate startDate = getWeeklyStartDate(endDate);

        List<DailyTaskWeeklyRecord> dailyRecords = buildDailyWeeklyRecords(user, startDate);
        List<WeeklyTaskWeeklyRecord> weeklyRecords = buildWeeklyWeeklyRecords(user, startDate);

        storage.saveUser(user);
        return new WeeklyUserHistory(userName, startDate, endDate, dailyRecords, weeklyRecords);
    }

    /**
     * Returns the start date of the 7-day history window.
     *
     * @param endDate End date of the window.
     * @return Start date of the window.
     */
    private LocalDate getWeeklyStartDate(LocalDate endDate) {
        return endDate.minusDays(WEEK_LENGTH_DAYS - 1);
    }

    /**
     * Builds weekly records for all daily routines.
     *
     * @param user User to read from.
     * @param startDate Start date of the weekly range.
     * @return List of daily task weekly records.
     */
    private List<DailyTaskWeeklyRecord> buildDailyWeeklyRecords(User user, LocalDate startDate) {
        List<DailyTaskWeeklyRecord> records = new ArrayList<>();

        for (Task task : user.getDailyRoutines().getAllTasks()) {
            DailyTaskWeeklyRecord record = buildDailyTaskWeeklyRecord(
                    user,
                    task.getDescription(),
                    startDate
            );
            records.add(record);
        }

        return records;
    }

    /**
     * Builds weekly records for all weekly routines.
     *
     * @param user User to read from.
     * @param startDate Start date of the weekly range.
     * @return List of weekly task weekly records.
     */
    private List<WeeklyTaskWeeklyRecord> buildWeeklyWeeklyRecords(User user, LocalDate startDate) {
        List<WeeklyTaskWeeklyRecord> records = new ArrayList<>();

        for (Task task : user.getWeeklyRoutines().getAllTasks()) {
            WeeklyTaskWeeklyRecord record = buildWeeklyTaskWeeklyRecord(
                    user,
                    task.getDescription(),
                    startDate
            );
            records.add(record);
        }

        return records;
    }

    /**
     * Builds the weekly record for one daily task.
     *
     * @param user User to read from.
     * @param taskName Name of the task.
     * @param startDate Start date of the weekly range.
     * @return Daily task weekly record.
     */
    private DailyTaskWeeklyRecord buildDailyTaskWeeklyRecord(
            User user,
            String taskName,
            LocalDate startDate
    ) {
        int completedCount = 0;
        List<String> missedDays = new ArrayList<>();

        for (int i = 0; i < WEEK_LENGTH_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            Day day = user.getDay(date);
            boolean done = isTaskCompleted(day, taskName, true);

            if (done) {
                completedCount++;
            } else {
                missedDays.add(formatDayName(date));
            }
        }

        return new DailyTaskWeeklyRecord(taskName, completedCount, missedDays);
    }

    /**
     * Builds the weekly record for one weekly task.
     *
     * @param user User to read from.
     * @param taskName Name of the task.
     * @param startDate Start date of the weekly range.
     * @return Weekly task weekly record.
     */
    private WeeklyTaskWeeklyRecord buildWeeklyTaskWeeklyRecord(
            User user,
            String taskName,
            LocalDate startDate
    ) {
        boolean done = false;
        List<String> doneDays = new ArrayList<>();

        for (int i = 0; i < WEEK_LENGTH_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            Day day = user.getDay(date);
            boolean marked = isTaskCompleted(day, taskName, false);

            if (marked) {
                done = true;
                doneDays.add(formatDayName(date));
            }
        }

        return new WeeklyTaskWeeklyRecord(taskName, done, doneDays);
    }

    /**
     * Returns whether the given task is completed on the given day.
     *
     * @param day Day record to check.
     * @param taskName Name of the task.
     * @param isDaily True for daily task, false for weekly task.
     * @return True if completed, otherwise false.
     */
    private boolean isTaskCompleted(Day day, String taskName, boolean isDaily) {
        if (day == null) {
            return false;
        }

        if (isDaily) {
            return day.isDailyCompleted(taskName);
        } else {
            return day.isWeeklyCompleted(taskName);
        }
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

        /**
         * Creates a task completion status.
         *
         * @param taskName Name of the task.
         * @param completed Whether the task is completed.
         */
        public TaskStatus(String taskName, boolean completed) {
            this.taskName = taskName;
            this.completed = completed;
        }

        /**
         * Returns the task name.
         *
         * @return Task name.
         */
        public String getTaskName() {
            return taskName;
        }

        /**
         * Returns whether the task is completed.
         *
         * @return True if completed, otherwise false.
         */
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

        /**
         * Creates today's history for one user.
         *
         * @param userName Name of the user.
         * @param dailyTasks Daily task statuses.
         * @param weeklyTasks Weekly task statuses.
         */
        public TodayUserHistory(String userName, List<TaskStatus> dailyTasks, List<TaskStatus> weeklyTasks) {
            this.userName = userName;
            this.dailyTasks = dailyTasks;
            this.weeklyTasks = weeklyTasks;
        }

        /**
         * Returns the user name.
         *
         * @return User name.
         */
        public String getUserName() {
            return userName;
        }

        /**
         * Returns the daily task statuses.
         *
         * @return Daily task statuses.
         */
        public List<TaskStatus> getDailyTasks() {
            return dailyTasks;
        }

        /**
         * Returns the weekly task statuses.
         *
         * @return Weekly task statuses.
         */
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

        /**
         * Creates weekly history for one daily task.
         *
         * @param taskName Name of the task.
         * @param completedCount Number of days completed.
         * @param missedDays Days on which the task was missed.
         */
        public DailyTaskWeeklyRecord(String taskName, int completedCount, List<String> missedDays) {
            this.taskName = taskName;
            this.completedCount = completedCount;
            this.missedDays = missedDays;
        }

        /**
         * Returns the task name.
         *
         * @return Task name.
         */
        public String getTaskName() {
            return taskName;
        }

        /**
         * Returns the completed count.
         *
         * @return Number of completed days.
         */
        public int getCompletedCount() {
            return completedCount;
        }

        /**
         * Returns the missed days.
         *
         * @return Missed day names.
         */
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

        /**
         * Creates weekly history for one weekly task.
         *
         * @param taskName Name of the task.
         * @param doneThisWeek Whether the task was done this week.
         * @param doneDays Days on which the task was marked done.
         */
        public WeeklyTaskWeeklyRecord(String taskName, boolean doneThisWeek, List<String> doneDays) {
            this.taskName = taskName;
            this.doneThisWeek = doneThisWeek;
            this.doneDays = doneDays;
        }

        /**
         * Returns the task name.
         *
         * @return Task name.
         */
        public String getTaskName() {
            return taskName;
        }

        /**
         * Returns whether the task was done this week.
         *
         * @return True if done this week, otherwise false.
         */
        public boolean isDoneThisWeek() {
            return doneThisWeek;
        }

        /**
         * Returns the days on which the task was marked done.
         *
         * @return Done day names.
         */
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

        /**
         * Creates the full weekly history for one user.
         *
         * @param userName Name of the user.
         * @param startDate Start date of the history window.
         * @param endDate End date of the history window.
         * @param dailyRecords Daily routine records.
         * @param weeklyRecords Weekly routine records.
         */
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

        /**
         * Returns the user name.
         *
         * @return User name.
         */
        public String getUserName() {
            return userName;
        }

        /**
         * Returns the start date.
         *
         * @return Start date.
         */
        public LocalDate getStartDate() {
            return startDate;
        }

        /**
         * Returns the end date.
         *
         * @return End date.
         */
        public LocalDate getEndDate() {
            return endDate;
        }

        /**
         * Returns the daily records.
         *
         * @return Daily routine records.
         */
        public List<DailyTaskWeeklyRecord> getDailyRecords() {
            return dailyRecords;
        }

        /**
         * Returns the weekly records.
         *
         * @return Weekly routine records.
         */
        public List<WeeklyTaskWeeklyRecord> getWeeklyRecords() {
            return weeklyRecords;
        }
    }
}
