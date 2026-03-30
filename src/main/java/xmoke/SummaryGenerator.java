package xmoke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SummaryGenerator {
    private static final Path REPORT_ROOT = Paths.get("report");

    public Path generateMonthlySummary(User user) {
        try {
            Files.createDirectories(REPORT_ROOT);

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29);

            String safeUserName = user.getName().trim().replace(" ", "_");
            String fileName = safeUserName + "_summary_" + endDate + ".csv";
            Path reportPath = REPORT_ROOT.resolve(fileName);

            List<String> lines = new ArrayList<>();

            // Header
            lines.add("Report Type,Monthly Summary");
            lines.add("User," + escape(user.getName()));
            lines.add("Period," + startDate + " to " + endDate);
            lines.add("Generated On," + LocalDate.now());
            lines.add("");

            // Summary section
            lines.add("Routine Name,Routine Type,Completed Count,Expected Count,Completion Rate");

            for (Task task : user.getDailyRoutines().getAllTasks()) {
                int completed = 0;
                int expected = 30;

                for (int i = 0; i < 30; i++) {
                    LocalDate date = startDate.plusDays(i);
                    Day day = user.getDay(date);
                    if (day != null && day.isDailyCompleted(task.getDescription())) {
                        completed++;
                    }
                }

                String rate = expected == 0 ? "0%" : (completed * 100 / expected) + "%";
                lines.add(csvRow(
                        task.getDescription(),
                        "DAILY",
                        String.valueOf(completed),
                        String.valueOf(expected),
                        rate
                ));
            }

            int weeklyExpected = countWeeksInRange(startDate, endDate);
            for (Task task : user.getWeeklyRoutines().getAllTasks()) {
                int completedWeeks = 0;

                LocalDate cursor = startDate;
                while (!cursor.isAfter(endDate)) {
                    LocalDate weekEnd = cursor.plusDays(6);
                    if (weekEnd.isAfter(endDate)) {
                        weekEnd = endDate;
                    }

                    boolean doneThisWeek = false;
                    LocalDate d = cursor;
                    while (!d.isAfter(weekEnd)) {
                        Day day = user.getDay(d);
                        if (day != null && day.isWeeklyCompleted(task.getDescription())) {
                            doneThisWeek = true;
                            break;
                        }
                        d = d.plusDays(1);
                    }

                    if (doneThisWeek) {
                        completedWeeks++;
                    }

                    cursor = weekEnd.plusDays(1);
                }

                String rate = weeklyExpected == 0 ? "0%" : (completedWeeks * 100 / weeklyExpected) + "%";
                lines.add(csvRow(
                        task.getDescription(),
                        "WEEKLY",
                        String.valueOf(completedWeeks),
                        String.valueOf(weeklyExpected),
                        rate
                ));
            }

            lines.add("");
            lines.add("Date,Daily Log");

            for (int i = 0; i < 30; i++) {
                LocalDate date = startDate.plusDays(i);
                Day day = user.getDay(date);
                String log = (day == null) ? "" : day.getLog();
                lines.add(csvRow(date.toString(), log));
            }

            // Detailed completion history section
            lines.add("");
            lines.add("Detailed Completion History");

            // Header row: Task + dates
            List<String> dateHeader = new ArrayList<>();
            dateHeader.add("Task");
            for (int i = 0; i < 30; i++) {
                LocalDate date = startDate.plusDays(i);
                dateHeader.add(date.toString());
            }
            lines.add(csvRow(dateHeader.toArray(new String[0])));

            // Daily tasks rows
            for (Task task : user.getDailyRoutines().getAllTasks()) {
                List<String> row = new ArrayList<>();
                row.add(task.getDescription() + " (DAILY)");
                for (int i = 0; i < 30; i++) {
                    LocalDate date = startDate.plusDays(i);
                    Day day = user.getDay(date);
                    if (day != null && day.isDailyCompleted(task.getDescription())) {
                        row.add("✓");
                    } else {
                        row.add("");
                    }
                }
                lines.add(csvRow(row.toArray(new String[0])));
            }

            // Weekly tasks rows
            for (Task task : user.getWeeklyRoutines().getAllTasks()) {
                List<String> row = new ArrayList<>();
                row.add(task.getDescription() + " (WEEKLY)");
                for (int i = 0; i < 30; i++) {
                    LocalDate date = startDate.plusDays(i);
                    Day day = user.getDay(date);
                    if (day != null && day.isWeeklyCompleted(task.getDescription())) {
                        row.add("✓");
                    } else {
                        row.add("");
                    }
                }
                lines.add(csvRow(row.toArray(new String[0])));
            }

            // Daily log row at bottom
            List<String> logRow = new ArrayList<>();
            logRow.add("Daily Log");
            for (int i = 0; i < 30; i++) {
                LocalDate date = startDate.plusDays(i);
                Day day = user.getDay(date);
                logRow.add(day == null ? "" : day.getLog());
            }
            lines.add(csvRow(logRow.toArray(new String[0])));

            Files.write(reportPath, lines);
            return reportPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate summary: " + e.getMessage(), e);
        }
    }

    private int countWeeksInRange(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            count++;
            cursor = cursor.plusDays(7);
        }
        return count;
    }

    private String csvRow(String... values) {
        List<String> escaped = new ArrayList<>();
        for (String value : values) {
            escaped.add(escape(value));
        }
        return String.join(",", escaped);
    }

    private String escape(String value) {
        if (value == null) {
            return "\"\"";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}