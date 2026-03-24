package xmoke;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user input into commands and extracts parameters such as task indexes and date/time values.
 */

public class Parser {

    /** Hour and minute used when a date is given without time (end of day). */
    private static final int DEFAULT_END_OF_DAY_HOUR = 23;
    private static final int DEFAULT_END_OF_DAY_MINUTE = 59;

    /**
     * Parses a 1-based task index from the input and converts it into a 0-based index.
     *
     * @param indexText The index string from the user (e.g. "2").
     * @param taskCount Number of tasks currently in the list.
     * @return The 0-based task index.
     * @throws NumberFormatException If the index is not a valid integer.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public static int parseTaskIndex(String indexText, int taskCount) throws NumberFormatException {
        String trimmed = indexText.trim();
        int oneBasedIndex = Integer.parseInt(trimmed);
        int zeroBasedIndex = oneBasedIndex - 1;

        if (zeroBasedIndex < 0 || zeroBasedIndex >= taskCount) {
            throw new IndexOutOfBoundsException("Task number is out of range.");
        }
        assert zeroBasedIndex >= 0 && zeroBasedIndex < taskCount : "index must be in valid range after check";

        return zeroBasedIndex;
    }

    /**
     * Parse date and time from string.
     * Accepts formats:
     * - yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)
     * - yyyy-MM-dd (e.g., 2019-12-02) - defaults to 23:59
     * - d/M/yyyy HHmm (e.g., 2/12/2019 1800)
     * - d/M/yyyy (e.g., 2/12/2019) - defaults to 23:59
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        dateTimeStr = dateTimeStr.trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mma");
            return LocalDateTime.parse(dateTimeStr.toUpperCase(), formatter);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy h:mma");
            return LocalDateTime.parse(dateTimeStr.toUpperCase(), formatter);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
            return LocalDate.now().atTime(java.time.LocalTime.parse(dateTimeStr.toUpperCase(), formatter));
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalDate.now().atTime(java.time.LocalTime.parse(dateTimeStr, formatter));
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateTimeStr, formatter);
            return date.atTime(DEFAULT_END_OF_DAY_HOUR, DEFAULT_END_OF_DAY_MINUTE);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate date = LocalDate.parse(dateTimeStr, formatter);
            return date.atTime(DEFAULT_END_OF_DAY_HOUR, DEFAULT_END_OF_DAY_MINUTE);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Unable to parse date/time: " + dateTimeStr, dateTimeStr, 0);
        }
    }

    /** Parses a date string (yyyy-MM-dd or d/M/yyyy). */
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        dateStr = dateStr.trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            // Try the next format.
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Unable to parse date: " + dateStr, dateStr, 0);
        }
    }
}
