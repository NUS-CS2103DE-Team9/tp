package HealthcareEveryday.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.User;

/**
 * Generates summary reports for users.
 */
public class SummaryGenerator {
    private static final Path REPORT_ROOT = Paths.get("report");
    private static final int MONTHLY_PERIOD_DAYS = 30;

    /**
     * Generates a monthly summary report for the given user.
     *
     * @param user User to generate the report for.
     * @return Path to the generated report.
     */
    public Path generateMonthlySummary(User user) {
        try {
            Files.createDirectories(REPORT_ROOT);

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(MONTHLY_PERIOD_DAYS - 1);
            Path reportPath = buildReportPath(user, endDate);

            List<String> lines = new ArrayList<>();
            addReportHeader(lines, user, startDate, endDate);
            addRoutineSummarySection(lines, user, startDate, endDate);
            addDailyLogSection(lines, user, startDate);
            addDetailedHistorySection(lines, user, startDate);

            Files.write(reportPath, lines);
            return reportPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate summary: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the output path for the user's report.
     *
     * @param user User whose report is being generated.
     * @param endDate End date of the report period.
     * @return Output report path.
     */
    private Path buildReportPath(User user, LocalDate endDate) {
        String safeUserName = user.getName().trim().replace(" ", "_");
        String fileName = safeUserName + "_summary_" + endDate + ".csv";
        return REPORT_ROOT.resolve(fileName);
    }

    /**
     * Adds the report header section.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     * @param endDate End date of the report period.
     */
    private void addReportHeader(List<String> lines, User user, LocalDate startDate, LocalDate endDate) {
        lines.add("Report Type,Monthly Summary");
        lines.add("User," + escape(user.getName()));
        lines.add("Period," + startDate + " to " + endDate);
        lines.add("Generated On," + LocalDate.now());
        lines.add("");
    }

    /**
     * Adds the routine completion summary section.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     * @param endDate End date of the report period.
     */
    private void addRoutineSummarySection(
            List<String> lines,
            User user,
            LocalDate startDate,
            LocalDate endDate
    ) {
        lines.add("Routine Name,Routine Type,Completed Count,Expected Count,Completion Rate");

        addDailyRoutineSummary(lines, user, startDate);
        addWeeklyRoutineSummary(lines, user, startDate, endDate);
    }

    /**
     * Adds summary rows for all daily routines.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     */
    private void addDailyRoutineSummary(List<String> lines, User user, LocalDate startDate) {
        for (Task task : user.getDailyRoutines().getAllTasks()) {
            int completed = countDailyCompletions(user, task.getDescription(), startDate);
            int expected = MONTHLY_PERIOD_DAYS;
            String rate = formatRate(completed, expected);

            lines.add(csvRow(
                    task.getDescription(),
                    "DAILY",
                    String.valueOf(completed),
                    String.valueOf(expected),
                    rate
            ));
        }
    }

    /**
     * Adds summary rows for all weekly routines.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     * @param endDate End date of the report period.
     */
    private void addWeeklyRoutineSummary(
            List<String> lines,
            User user,
            LocalDate startDate,
            LocalDate endDate
    ) {
        int weeklyExpected = countWeeksInRange(startDate, endDate);

        for (Task task : user.getWeeklyRoutines().getAllTasks()) {
            int completedWeeks = countWeeklyCompletions(user, task.getDescription(), startDate, endDate);
            String rate = formatRate(completedWeeks, weeklyExpected);

            lines.add(csvRow(
                    task.getDescription(),
                    "WEEKLY",
                    String.valueOf(completedWeeks),
                    String.valueOf(weeklyExpected),
                    rate
            ));
        }
    }

    /**
     * Adds the daily log section.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     */
    private void addDailyLogSection(List<String> lines, User user, LocalDate startDate) {
        lines.add("");
        lines.add("Date,Daily Log");

        for (int i = 0; i < MONTHLY_PERIOD_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            Day day = user.getDay(date);
            String log = day == null ? "" : day.getLog();
            lines.add(csvRow(date.toString(), log));
        }
    }

    /**
     * Adds the detailed completion history section.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     */
    private void addDetailedHistorySection(List<String> lines, User user, LocalDate startDate) {
        lines.add("");
        lines.add("Detailed Completion History");
        lines.add(buildDateHeaderRow(startDate));

        addDailyHistoryRows(lines, user, startDate);
        addWeeklyHistoryRows(lines, user, startDate);
        addLogHistoryRow(lines, user, startDate);
    }

    /**
     * Builds the date header row for the detailed history section.
     *
     * @param startDate Start date of the report period.
     * @return CSV row containing the date header.
     */
    private String buildDateHeaderRow(LocalDate startDate) {
        List<String> header = new ArrayList<>();
        header.add("Task");

        for (int i = 0; i < MONTHLY_PERIOD_DAYS; i++) {
            header.add(startDate.plusDays(i).toString());
        }

        return csvRow(header.toArray(new String[0]));
    }

    /**
     * Adds detailed history rows for daily routines.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     */
    private void addDailyHistoryRows(List<String> lines, User user, LocalDate startDate) {
        for (Task task : user.getDailyRoutines().getAllTasks()) {
            lines.add(buildTaskHistoryRow(user, task.getDescription(), "DAILY", startDate));
        }
    }

    /**
     * Adds detailed history rows for weekly routines.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     */
    private void addWeeklyHistoryRows(List<String> lines, User user, LocalDate startDate) {
        for (Task task : user.getWeeklyRoutines().getAllTasks()) {
            lines.add(buildTaskHistoryRow(user, task.getDescription(), "WEEKLY", startDate));
        }
    }

    /**
     * Builds one detailed history row for a task.
     *
     * @param user User being reported on.
     * @param taskName Name of the task.
     * @param routineType Type of the routine ("DAILY" or "WEEKLY").
     * @param startDate Start date of the report period.
     * @return CSV row for the task history.
     */
    private String buildTaskHistoryRow(User user, String taskName, String routineType, LocalDate startDate) {
        List<String> row = new ArrayList<>();
        row.add(taskName + " (" + routineType + ")");

        for (int i = 0; i < MONTHLY_PERIOD_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            Day day = user.getDay(date);

            if (isTaskCompleted(day, taskName, routineType)) {
                row.add("Y");
            } else {
                row.add("");
            }
        }

        return csvRow(row.toArray(new String[0]));
    }

    /**
     * Adds the daily log history row in the detailed history section.
     *
     * @param lines Lines of the CSV file.
     * @param user User being reported on.
     * @param startDate Start date of the report period.
     */
    private void addLogHistoryRow(List<String> lines, User user, LocalDate startDate) {
        List<String> row = new ArrayList<>();
        row.add("Daily Log");

        for (int i = 0; i < MONTHLY_PERIOD_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            Day day = user.getDay(date);
            row.add(day == null ? "" : day.getLog());
        }

        lines.add(csvRow(row.toArray(new String[0])));
    }

    /**
     * Counts how many times a daily task was completed in the report period.
     *
     * @param user User being reported on.
     * @param taskName Name of the task.
     * @param startDate Start date of the report period.
     * @return Number of completed days.
     */
    private int countDailyCompletions(User user, String taskName, LocalDate startDate) {
        int completed = 0;

        for (int i = 0; i < MONTHLY_PERIOD_DAYS; i++) {
            LocalDate date = startDate.plusDays(i);
            Day day = user.getDay(date);

            if (day != null && day.isDailyCompleted(taskName)) {
                completed++;
            }
        }

        return completed;
    }

    /**
     * Counts how many weeks a weekly task was completed in the report period.
     *
     * @param user User being reported on.
     * @param taskName Name of the task.
     * @param startDate Start date of the report period.
     * @param endDate End date of the report period.
     * @return Number of completed weeks.
     */
    private int countWeeklyCompletions(User user, String taskName, LocalDate startDate, LocalDate endDate) {
        int completedWeeks = 0;
        LocalDate cursor = startDate;

        while (!cursor.isAfter(endDate)) {
            LocalDate weekEnd = cursor.plusDays(6);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }

            if (wasWeeklyTaskDoneInRange(user, taskName, cursor, weekEnd)) {
                completedWeeks++;
            }

            cursor = weekEnd.plusDays(1);
        }

        return completedWeeks;
    }

    /**
     * Checks whether a weekly task was completed at least once within a date range.
     *
     * @param user User being reported on.
     * @param taskName Name of the task.
     * @param startDate Start date of the week block.
     * @param endDate End date of the week block.
     * @return True if completed at least once.
     */
    private boolean wasWeeklyTaskDoneInRange(User user, String taskName, LocalDate startDate, LocalDate endDate) {
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            Day day = user.getDay(date);
            if (day != null && day.isWeeklyCompleted(taskName)) {
                return true;
            }
            date = date.plusDays(1);
        }

        return false;
    }

    /**
     * Returns whether the given task is completed on the given day.
     *
     * @param day Day record to check.
     * @param taskName Name of the task.
     * @param routineType Type of the routine ("DAILY" or "WEEKLY").
     * @return True if completed, otherwise false.
     */
    private boolean isTaskCompleted(Day day, String taskName, String routineType) {
        if (day == null) {
            return false;
        }

        if ("DAILY".equals(routineType)) {
            return day.isDailyCompleted(taskName);
        }

        return day.isWeeklyCompleted(taskName);
    }

    /**
     * Formats a completion rate.
     *
     * @param completed Number completed.
     * @param expected Number expected.
     * @return Percentage string.
     */
    private String formatRate(int completed, int expected) {
        if (expected == 0) {
            return "0%";
        }
        return (completed * 100 / expected) + "%";
    }

    /**
     * Counts the number of weeks covered in the given date range.
     *
     * @param startDate Start date of the range.
     * @param endDate End date of the range.
     * @return Number of weeks in the range.
     */
    private int countWeeksInRange(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate cursor = startDate;

        while (!cursor.isAfter(endDate)) {
            count++;
            cursor = cursor.plusDays(7);
        }

        return count;
    }

    /**
     * Builds a CSV row from the given values.
     *
     * @param values Values to include in the row.
     * @return CSV-formatted row.
     */
    private String csvRow(String... values) {
        List<String> escaped = new ArrayList<>();
        for (String value : values) {
            escaped.add(escape(value));
        }
        return String.join(",", escaped);
    }

    /**
     * Escapes a value for safe inclusion in a CSV file.
     *
     * @param value Value to escape.
     * @return Escaped CSV value.
     */
    private String escape(String value) {
        if (value == null) {
            return "\"\"";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
