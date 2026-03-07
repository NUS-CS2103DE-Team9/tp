package xmoke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles loading tasks from disk and saving tasks to disk in a simple text-based format.
 */

public class Storage {
    private static final Path DATA_ROOT = Paths.get("data");
    private static final String LEGACY_USER_FOLDER = "Obi-Wan_Kenobi";
    private static final String[] CHEER_QUOTES = {
        "Do. Or do not. There is no try.",
        "Patience you must have my young Padawan.",
        "Train yourself to let go of everything you fear to lose.",
        "Always pass on what you have learned.",
        "Feel the force!",
        "A Jedi uses the Force for knowledge and defense, never for attack.",
        "Difficult to see. Always in motion is the future.",
        "Fear is the path to the dark side. Fear leads to anger. Anger leads to hate. Hate leads to suffering.",
        "Your path you must decide."
    };

    private final Path dataFolderPath;
    private final Path dataFilePath;
    private final Path cheerFilePath;

    /** Uses default shared data folder (legacy). */
    public Storage() {
        this.dataFolderPath = DATA_ROOT;
        this.dataFilePath = DATA_ROOT.resolve("XMOKE.txt");
        this.cheerFilePath = DATA_ROOT.resolve("cheer.txt");
        ensureDataFileExists();
    }

    /**
     * Uses a user-specific folder under data/ so each user has separate task data.
     *
     * @param userDisplayName Display name (e.g. "Obi-Wan Kenobi"); spaces become underscores in path.
     */
    public Storage(String userDisplayName) {
        String folder = userDisplayName.trim().replace(" ", "_");
        this.dataFolderPath = DATA_ROOT.resolve(folder);
        this.dataFilePath = dataFolderPath.resolve("XMOKE.txt");
        this.cheerFilePath = dataFolderPath.resolve("cheer.txt");
        ensureDataFileExists();
        migrateLegacyDataIfNeeded(folder);
    }

    /**
     * Ensures the data folder and data file exist. Creates them if missing.
     *
     * @throws IOException If folder/file creation fails.
     */

    private void ensureDataFileExists() {
        try {
            Files.createDirectories(dataFolderPath);
            if (Files.notExists(dataFilePath)) {
                Files.createFile(dataFilePath);
            }
            if (Files.notExists(cheerFilePath)) {
                Files.createFile(cheerFilePath);
            }
        } catch (IOException e) {
            System.out.println("OOPS!!! I couldn't set up the data file: " + e.getMessage());
        }
    }

    /** One-time migration: if this user is Obi-Wan and legacy data exists, copy it into user folder. */
    private void migrateLegacyDataIfNeeded(String userFolder) {
        if (!LEGACY_USER_FOLDER.equals(userFolder)) {
            return;
        }
        Path legacyData = DATA_ROOT.resolve("XMOKE.txt");
        try {
            if (Files.exists(legacyData) && (!Files.exists(dataFilePath) || Files.size(dataFilePath) == 0)) {
                Files.copy(legacyData, dataFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.out.println("OOPS!!! Could not migrate legacy data: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from the data file.
     *
     * @return A list of tasks loaded from disk.
     * @throws IOException If reading the file fails.
     */

    public TaskList loadTasks() {
        TaskList taskList = new TaskList();

        try {
            List<String> lines = Files.readAllLines(dataFilePath);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length < 3) {
                    continue;
                }

                Task.TaskType type;
                try {
                    type = Task.TaskType.valueOf(parts[0].trim());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                boolean done = parts[1].trim().equals("1");
                String description = parts[2].trim();

                LocalDateTime dateTime = null;
                if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                    try {
                        dateTime = LocalDateTime.parse(parts[3].trim());
                    } catch (DateTimeParseException e) {
                        // If parsing fails, dateTime remains null
                    }
                }

                Task task = new Task(description, type, done, dateTime);
                try {
                    taskList.addTask(task);
                } catch (IllegalStateException e) {
                    // List is full (e.g. file has more than MAX_TASKS); stop adding, keep already-loaded tasks
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("OOPS!!! I couldn't load saved data: " + e.getMessage());
        }

        return taskList;
    }

    /**
     * Saves the given tasks to disk.
     *
     * @param taskList Task list to save.
     */

    public void saveTasks(TaskList taskList) {
        try {
            StringBuilder content = new StringBuilder();
            for (Task task : taskList.getAllTasks()) {
                content.append(task.toFileFormat()).append("\n");
            }
            Files.writeString(dataFilePath, content.toString());
        } catch (IOException e) {
            System.out.println("OOPS!!! I couldn't save data: " + e.getMessage());
        }
    }

    /** Returns a random cheer quote from the fixed list of nine messages. */
    public String getRandomCheerQuote() {
        int index = (int) (Math.random() * CHEER_QUOTES.length);
        return CHEER_QUOTES[index];
    }
}
