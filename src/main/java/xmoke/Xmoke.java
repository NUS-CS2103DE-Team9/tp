package xmoke;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Entry point of the XMOKE chatbot application.
 * Handles the program startup flow and delegates user interactions to other components.
 */
public class Xmoke {
    private static final List<String> FIXED_ROUTINE_ITEMS = Arrays.asList(
            "breakfast", "lunch", "dinner", "medication1", "medication2");

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /** Creates Xmoke with default storage path and loads tasks from disk. */
    public Xmoke() {
        ui = new Ui();
        storage = new Storage();
        tasks = storage.loadTasks();
    }

    /** Creates Xmoke with the given storage (e.g. for a specific user). */
    public Xmoke(Storage storage) {
        ui = new Ui();
        this.storage = storage;
        this.tasks = storage.loadTasks();
    }

    /**
     * Generates a response for the given user input.
     * Used by the GUI to get a single reply for a single message.
     * Command order below matters: specific command patterns are checked before fallback errors.
     *
     * @param input The user's input string.
     * @return The response string to display.
     */
    public String getResponse(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.equalsIgnoreCase("bye")) {
            return ui.getGoodbyeMessage();
        }

        if (trimmedInput.startsWith("record ")) {
            return handleRecord(trimmedInput);
        }

        if (trimmedInput.equals("record")) {
            return ui.getErrorMessage("OOPS!!! Please provide a fixed routine item to record.");
        }

        if (trimmedInput.startsWith("skip ")) {
            return handleSkip(trimmedInput);
        }

        if (trimmedInput.equals("skip")) {
            return ui.getErrorMessage("OOPS!!! Please provide a fixed routine item to skip.");
        }

        if (trimmedInput.startsWith("symptom ")) {
            return handleSymptom(trimmedInput);
        }

        if (trimmedInput.equals("symptom")) {
            return ui.getErrorMessage("OOPS!!! Please provide symptom description and severity 1-10.");
        }

        if (trimmedInput.startsWith("exercise ")) {
            return handleExercise(trimmedInput);
        }

        if (trimmedInput.equals("exercise")) {
            return ui.getErrorMessage("OOPS!!! Please provide an exercise description.");
        }

        if (trimmedInput.startsWith("note ")) {
            return handleNote(trimmedInput);
        }

        if (trimmedInput.equals("note")) {
            return ui.getErrorMessage("OOPS!!! Please provide a note description.");
        }

        if (trimmedInput.startsWith("deadline ")) {
            return handleHealthDeadline(trimmedInput);
        }

        if (trimmedInput.equals("list")) {
            return buildTodayHealthList();
        }

        return ui.getErrorMessage("OOPS!!! Unknown command. Use record/skip/symptom/exercise/note/deadline/list/bye.");
    }

    /**
     * Runs the text-based UI loop: show logo and welcome, then read commands from stdin,
     * process each (same commands as getResponse), and exit on "bye".
     */
    public void run() {
        ui.showLogo();
        ui.showWelcome();

        while (true) {
            String input = ui.readCommand();
            System.out.print(getResponse(input));
            if (input.trim().equalsIgnoreCase("bye")) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Runs the XMOKE chatbot.
     *
     * @param args Command-line arguments (not used).
     */

    public static void main(String... args) {
        new Xmoke().run();
    }

    private String handleRecord(String input) {
        String remainder = input.substring("record ".length()).trim();
        if (remainder.isEmpty()) {
            return ui.getErrorMessage("OOPS!!! Please provide a fixed routine item to record.");
        }
        String[] parts = remainder.split("\\s+", 2);
        String item = parts[0].toLowerCase();
        String note = parts.length > 1 ? parts[1].trim() : "";
        if (!FIXED_ROUTINE_ITEMS.contains(item)) {
            return ui.getErrorMessage("OOPS!!! Unknown fixed item. Use breakfast/lunch/dinner/medication1/medication2.");
        }
        removeExistingRoutineStatusForToday(item);
        String payload = "ROUTINE|" + item + "|DONE|" + note;
        tasks.addTask(new Task(payload, Task.TaskType.T, LocalDateTime.now()));
        storage.saveTasks(tasks);
        String noteMsg = note.isEmpty() ? "" : " Note: " + note;
        return ui.getErrorMessage("Recorded " + item + " as done." + noteMsg);
    }

    private String handleSkip(String input) {
        String remainder = input.substring("skip ".length()).trim();
        if (remainder.isEmpty()) {
            return ui.getErrorMessage("OOPS!!! Please provide a fixed routine item to skip.");
        }
        String[] parts = remainder.split("\\s+", 2);
        String item = parts[0].toLowerCase();
        String reason = parts.length > 1 ? parts[1].trim() : "";
        if (!FIXED_ROUTINE_ITEMS.contains(item)) {
            return ui.getErrorMessage("OOPS!!! Unknown fixed item. Use breakfast/lunch/dinner/medication1/medication2.");
        }
        removeExistingRoutineStatusForToday(item);
        String payload = "ROUTINE|" + item + "|SKIPPED|" + reason;
        tasks.addTask(new Task(payload, Task.TaskType.T, LocalDateTime.now()));
        storage.saveTasks(tasks);
        String reasonMsg = reason.isEmpty() ? "" : " Reason: " + reason;
        return ui.getErrorMessage("Recorded " + item + " as skipped." + reasonMsg);
    }

    private String handleSymptom(String input) {
        String remainder = input.substring("symptom ".length()).trim();
        String[] parts = remainder.split("\\s+");
        if (parts.length < 2) {
            return ui.getErrorMessage("OOPS!!! Please provide symptom description and severity 1-10.");
        }
        String severityText = parts[parts.length - 1];
        int severity;
        try {
            severity = Integer.parseInt(severityText);
        } catch (NumberFormatException e) {
            return ui.getErrorMessage("OOPS!!! Symptom severity must be an integer from 1 to 10.");
        }
        if (severity < 1 || severity > 10) {
            return ui.getErrorMessage("OOPS!!! Symptom severity must be an integer from 1 to 10.");
        }
        String description = remainder.substring(0, remainder.length() - severityText.length()).trim();
        if (description.isEmpty()) {
            return ui.getErrorMessage("OOPS!!! Symptom description cannot be empty.");
        }
        String payload = "SYMPTOM|" + severity + "|" + description;
        tasks.addTask(new Task(payload, Task.TaskType.T, LocalDateTime.now()));
        storage.saveTasks(tasks);
        return ui.getErrorMessage("Symptom saved: " + description + " (severity " + severity + ").");
    }

    private String handleExercise(String input) {
        String description = input.substring("exercise ".length()).trim();
        if (description.isEmpty()) {
            return ui.getErrorMessage("OOPS!!! Please provide an exercise description.");
        }
        String payload = "EXERCISE|" + description;
        tasks.addTask(new Task(payload, Task.TaskType.T, LocalDateTime.now()));
        storage.saveTasks(tasks);
        return ui.getErrorMessage("Exercise saved: " + description);
    }

    private String handleNote(String input) {
        String text = input.substring("note ".length()).trim();
        if (text.isEmpty()) {
            return ui.getErrorMessage("OOPS!!! Please provide a note description.");
        }
        String payload = "NOTE|" + text;
        tasks.addTask(new Task(payload, Task.TaskType.T, LocalDateTime.now()));
        storage.saveTasks(tasks);
        return ui.getErrorMessage("Note saved.");
    }

    private String handleHealthDeadline(String input) {
        String remainder = input.substring("deadline ".length()).trim();
        if (remainder.isEmpty()) {
            return ui.getErrorMessage("OOPS!!! Deadline needs description and time.");
        }
        String description;
        String dateTimeText;
        if (remainder.contains(" /by ")) {
            String[] parts = remainder.split(" /by ", 2);
            description = parts[0].trim();
            dateTimeText = parts.length > 1 ? parts[1].trim() : "";
        } else {
            String[] parsed = splitDescriptionAndDateTime(remainder);
            if (parsed == null) {
                return ui.getErrorMessage("OOPS!!! Deadline needs description and valid time.");
            }
            description = parsed[0];
            dateTimeText = parsed[1];
        }
        if (description.isEmpty() || dateTimeText.isEmpty()) {
            return ui.getErrorMessage("OOPS!!! Deadline needs description and valid time.");
        }
        try {
            LocalDateTime dueAt = Parser.parseDateTime(dateTimeText);
            String payload = "DEADLINE|" + description;
            tasks.addTask(new Task(payload, Task.TaskType.D, dueAt));
            storage.saveTasks(tasks);
            return ui.getErrorMessage("Deadline saved: " + description);
        } catch (DateTimeParseException e) {
            return ui.getErrorMessage("OOPS!!! Invalid date/time format.");
        }
    }

    private String[] splitDescriptionAndDateTime(String text) {
        String[] tokens = text.split("\\s+");
        for (int i = 1; i < tokens.length; i++) {
            String description = String.join(" ", Arrays.copyOfRange(tokens, 0, i)).trim();
            String dateTimeText = String.join(" ", Arrays.copyOfRange(tokens, i, tokens.length)).trim();
            if (description.isEmpty() || dateTimeText.isEmpty()) {
                continue;
            }
            try {
                Parser.parseDateTime(dateTimeText);
                return new String[] {description, dateTimeText};
            } catch (DateTimeParseException e) {
                // try next split
            }
        }
        return null;
    }

    private void removeExistingRoutineStatusForToday(String item) {
        LocalDate today = LocalDate.now();
        Iterator<Task> iterator = tasks.getAllTasks().iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getDateTime() == null || !task.getDateTime().toLocalDate().equals(today)) {
                continue;
            }
            String desc = task.getDescription();
            if (desc.startsWith("ROUTINE|" + item + "|")) {
                iterator.remove();
            }
        }
    }

    private String buildTodayHealthList() {
        LocalDate today = LocalDate.now();
        StringBuilder sb = new StringBuilder();
        sb.append("\nToday's routine status:\n");
        for (String item : FIXED_ROUTINE_ITEMS) {
            String status = "[ ]";
            String stateLabel = "";
            String detail = "";
            for (Task task : tasks.getAllTasks()) {
                if (task.getDateTime() == null || !task.getDateTime().toLocalDate().equals(today)) {
                    continue;
                }
                String desc = task.getDescription();
                if (!desc.startsWith("ROUTINE|" + item + "|")) {
                    continue;
                }
                String[] parts = desc.split("\\|", 4);
                String state = parts.length > 2 ? parts[2] : "";
                detail = parts.length > 3 ? parts[3] : "";
                if ("DONE".equals(state)) {
                    status = "[X]";
                    stateLabel = "";
                } else {
                    status = "[ ]";
                    stateLabel = " (skipped)";
                }
            }
            sb.append(status).append(" ").append(item).append(stateLabel);
            if (!detail.isBlank()) {
                sb.append(" - ").append(detail);
            }
            sb.append("\n");
        }

        List<Task> todayDeadlines = new ArrayList<>();
        for (Task task : tasks.getAllTasks()) {
            if (task.getType() == Task.TaskType.D && task.getDateTime() != null
                    && task.getDateTime().toLocalDate().equals(today)) {
                todayDeadlines.add(task);
            }
        }
        sb.append("\nToday's deadlines:\n");
        if (todayDeadlines.isEmpty()) {
            sb.append("(none)\n");
        } else {
            for (Task deadline : todayDeadlines) {
                String description = deadline.getDescription();
                if (description.startsWith("DEADLINE|")) {
                    description = description.substring("DEADLINE|".length());
                }
                sb.append("- ").append(description)
                        .append(" (by: ").append(deadline.getDateTime()).append(")\n");
            }
        }
        return sb.toString();
    }
}
