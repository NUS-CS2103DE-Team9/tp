package xmoke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles reading and writing of all application data, including users,
 * routines, daily records, and caregiver credentials.
 */
public class Storage {
    private static final Path DATA_ROOT = Paths.get("data");
    private static final Path USERS_ROOT = DATA_ROOT.resolve("users");
    private static final Path APP_ROOT = DATA_ROOT.resolve("app");
    private static final Path CAREGIVER_FILE = APP_ROOT.resolve("caregiver.txt");

    /**
     * Initializes storage and ensures required folder structure exists.
     */
    public Storage() {
        ensureBaseStructure();
    }

    /**
     * Ensures base directories and files exist.
     */
    private void ensureBaseStructure() {
        try {
            Files.createDirectories(USERS_ROOT);
            Files.createDirectories(APP_ROOT);
            if (Files.notExists(CAREGIVER_FILE)) {
                Files.writeString(CAREGIVER_FILE, "caregiver");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage: " + e.getMessage(), e);
        }
    }

    /**
     * Returns a list of all senior user names.
     *
     * @return List of user names.
     */
    public List<String> listSeniorNames() {
        try (Stream<Path> stream = Files.list(USERS_ROOT)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(this::readUserDisplayName)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Checks whether a user exists.
     *
     * @param name Name of the user.
     * @return True if user exists, otherwise false.
     */
    public boolean userExists(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        return listSeniorNames().stream()
                .anyMatch(existing -> existing.equalsIgnoreCase(trimmed));
    }

    /**
     * Adds a new user.
     *
     * @param name Name of the user.
     * @return True if successfully added.
     */
    public boolean addUser(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        if (userExists(trimmed)) {
            return false;
        }

        User user = new User(trimmed);
        saveUser(user);
        return true;
    }

    /**
     * Validates caregiver password.
     *
     * @param password Input password.
     * @return True if correct.
     */
    public boolean validateCaregiverPassword(String password) {
        try {
            String stored = Files.readString(CAREGIVER_FILE).trim();
            return stored.equals(password);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Changes caregiver password.
     *
     * @param oldPassword Old password.
     * @param newPassword New password.
     * @return True if changed successfully.
     */
    public boolean changeCaregiverPassword(String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        if (!validateCaregiverPassword(oldPassword)) {
            return false;
        }
        try {
            Files.writeString(CAREGIVER_FILE, newPassword.trim());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Loads a user and all associated data.
     *
     * @param userName Name of the user.
     * @return Loaded user object.
     */
    public User loadUser(String userName) {
        Path userFolder = getUserFolder(userName);
        if (Files.notExists(userFolder)) {
            User user = new User(userName);
            saveUser(user);
            return user;
        }

        User user = new User(userName);

        loadRoutineFile(userFolder.resolve("dailyRoutines.txt"),
                user.getDailyRoutines(), RoutineType.DAILY);
        loadRoutineFile(userFolder.resolve("weeklyRoutines.txt"),
                user.getWeeklyRoutines(), RoutineType.WEEKLY);

        Path daysFolder = userFolder.resolve("days");
        if (Files.exists(daysFolder)) {
            try (Stream<Path> stream = Files.list(daysFolder)) {
                stream.filter(Files::isRegularFile)
                        .sorted(Comparator.comparing(Path::getFileName))
                        .forEach(path -> {
                            Day day = loadDayFile(path);
                            if (day != null) {
                                user.addDay(day);
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException("Failed to load day files for " + userName, e);
            }
        }

        user.getOrCreateDay(LocalDate.now());
        return user;
    }

    /**
     * Saves a user and all associated data.
     *
     * @param user User to save.
     */
    public void saveUser(User user) {
        Path userFolder = getUserFolder(user.getName());
        Path daysFolder = userFolder.resolve("days");

        try {
            Files.createDirectories(userFolder);
            Files.createDirectories(daysFolder);

            Files.writeString(userFolder.resolve("profile.txt"), user.getName());

            writeRoutineFile(userFolder.resolve("dailyRoutines.txt"), user.getDailyRoutines());
            writeRoutineFile(userFolder.resolve("weeklyRoutines.txt"), user.getWeeklyRoutines());

            for (Day day : user.getDays()) {
                saveDayFile(daysFolder.resolve(day.getDate() + ".txt"), day);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user " + user.getName(), e);
        }
    }

    /**
     * Loads routine tasks from a file.
     */
    private void loadRoutineFile(Path filePath, TaskList taskList, RoutineType type) {
        if (Files.notExists(filePath)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    taskList.addTask(new Task(trimmed, type));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load routine file: " + filePath, e);
        }
    }

    /**
     * Writes routine tasks to a file.
     */
    private void writeRoutineFile(Path filePath, TaskList taskList) throws IOException {
        List<String> lines = taskList.getAllTasks().stream()
                .map(Task::getDescription)
                .toList();
        Files.write(filePath, lines);
    }

    /**
     * Loads a single day file.
     */
    private Day loadDayFile(Path filePath) {
        try {
            LocalDate date = LocalDate.parse(filePath.getFileName().toString().replace(".txt", ""));
            Day day = new Day(date);

            List<String> lines = Files.readAllLines(filePath);
            String section = "";

            for (String line : lines) {
                if (line.startsWith("log=")) {
                    day.setLog(line.substring(4));
                } else if (line.equals("[daily]")) {
                    section = "daily";
                } else if (line.equals("[weekly]")) {
                    section = "weekly";
                } else if (!line.isBlank()) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String taskName = parts[0].trim();
                        boolean completed = Boolean.parseBoolean(parts[1].trim());

                        if (section.equals("daily")) {
                            day.setDailyCompleted(taskName, completed);
                        } else if (section.equals("weekly")) {
                            day.setWeeklyCompleted(taskName, completed);
                        }
                    }
                }
            }

            return day;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Saves a single day file.
     */
    private void saveDayFile(Path filePath, Day day) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("log=" + day.getLog());
        lines.add("[daily]");
        day.getDailyStatus().forEach((task, completed) ->
                lines.add(task + "=" + completed));
        lines.add("[weekly]");
        day.getWeeklyStatus().forEach((task, completed) ->
                lines.add(task + "=" + completed));
        Files.write(filePath, lines);
    }

    /**
     * Returns the folder path for a user.
     */
    private Path getUserFolder(String userName) {
        return USERS_ROOT.resolve(sanitizeName(userName));
    }

    /**
     * Reads display name of a user.
     */
    private String readUserDisplayName(Path userFolder) {
        Path profileFile = userFolder.resolve("profile.txt");
        if (Files.exists(profileFile)) {
            try {
                return Files.readString(profileFile).trim();
            } catch (IOException ignored) {
                // fall back
            }
        }
        return userFolder.getFileName().toString().replace("_", " ");
    }

    /**
     * Sanitizes user name for file system usage.
     */
    private String sanitizeName(String name) {
        return name.trim().replace(" ", "_");
    }
}
