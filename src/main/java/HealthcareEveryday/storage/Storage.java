package HealthcareEveryday.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.RoutineType;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.TaskList;
import HealthcareEveryday.model.User;

/**
 * Handles reading and writing of all application data, including users,
 * routines, daily records, and caregiver credentials.
 */
public class Storage {
    private static final String DAILY_SECTION_HEADER = "[daily]";
    private static final String WEEKLY_SECTION_HEADER = "[weekly]";
    private static final String DAILY_SECTION = "daily";
    private static final String WEEKLY_SECTION = "weekly";
    private static final String PROFILE_FILE_NAME = "profile.txt";
    private static final String DAILY_ROUTINES_FILE_NAME = "dailyRoutines.txt";
    private static final String WEEKLY_ROUTINES_FILE_NAME = "weeklyRoutines.txt";
    private static final String DAYS_FOLDER_NAME = "days";

    private final Path dataRoot;
    private final Path usersRoot;
    private final Path appRoot;
    private final Path caregiverFile;

    /**
     * Initializes storage and ensures required folder structure exists.
     */
    public Storage() {
        this(Paths.get("data"));
    }

    /**
     * Initializes storage rooted at the provided data path.
     *
     * @param dataRoot Root path for all persisted app data.
     */
    public Storage(Path dataRoot) {
        this.dataRoot = dataRoot;
        this.usersRoot = this.dataRoot.resolve("users");
        this.appRoot = this.dataRoot.resolve("app");
        this.caregiverFile = this.appRoot.resolve(
                "caregiver.txt"
        );
        ensureBaseStructure();
    }

    /**
     * Ensures base directories and files exist.
     */
    private void ensureBaseStructure() {
        try {
            Files.createDirectories(usersRoot);
            Files.createDirectories(appRoot);

            if (Files.notExists(caregiverFile)) {
                Files.writeString(caregiverFile, "caregiver");
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to initialize storage: "
                            + e.getMessage(), e);
        }
    }

    /**
     * Returns a list of all senior user names.
     *
     * @return List of user names.
     */
    public List<String> listSeniorNames() {
        try (Stream<Path> stream = Files.list(usersRoot)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(this::readUserDisplayName)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        } catch (IOException e) {
            throw new StorageException(
                    "Failed to list senior users.", e
            );
        }
    }

    /**
     * Checks whether a user exists.
     *
     * @param name Name of the user.
     * @return True if user exists, otherwise false.
     */
    public boolean userExists(String name) {
        String trimmed = normalizeName(name);

        if (trimmed.isEmpty()) {
            return false;
        }

        List<String> seniorNames = listSeniorNames();

        for (String existingName : seniorNames) {
            if (existingName.equalsIgnoreCase(trimmed)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a new user.
     *
     * @param name Name of the user.
     * @return True if successfully added.
     */
    public boolean addUser(String name) {
        String trimmed = normalizeName(name);

        if (trimmed.isEmpty()) {
            return false;
        }

        if (userExists(trimmed)) {
            return false;
        }

        createAndSaveUser(trimmed);
        return true;
    }

    /**
     * Deletes the specified user and all associated data.
     *
     * @param name Name of the user.
     * @return True if deleted successfully.
     */
    public boolean deleteUser(String name) {
        String trimmed = normalizeName(name);

        if (trimmed.isEmpty()) {
            return false;
        }

        if (!userExists(trimmed)) {
            return false;
        }

        try {
            deleteUserFolder(trimmed);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Validates caregiver password.
     *
     * @param password Input password.
     * @return True if correct.
     */
    public boolean validateCaregiverPassword(String password) {
        try {
            String stored = Files.readString(caregiverFile).trim();
            return stored.equals(password);
        } catch (IOException e) {
            throw new StorageException(
                    "Failed to validate caregiver password.", e
            );
        }
    }

    /**
     * Changes caregiver password.
     *
     * @param oldPassword Old password.
     * @param newPassword New password.
     * @return True if changed successfully.
     */
    public boolean changeCaregiverPassword(
            String oldPassword, String newPassword
    ) {
        if (newPassword == null) {
            return false;
        }

        if (newPassword.trim().isEmpty()) {
            return false;
        }

        if (!validateCaregiverPassword(oldPassword)) {
            return false;
        }

        try {
            Files.writeString(caregiverFile, newPassword.trim());
            return true;
        } catch (IOException e) {
            throw new StorageException(
                    "Failed to change caregiver password.", e
            );
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
            return createAndSaveUser(userName);
        }

        User user = new User(userName);
        loadUserRoutines(user, userFolder);
        loadUserDays(user, userFolder);
        ensureTodayExists(user);
        return user;
    }

    /**
     * Creates a new user, saves it, and returns it.
     *
     * @param userName Name of the user.
     * @return Newly created user.
     */
    private User createAndSaveUser(String userName) {
        User user = new User(userName);
        saveUser(user);
        return user;
    }

    /**
     * Loads all routine files for the given user.
     *
     * @param user User object to populate.
     * @param userFolder Folder containing the user's data.
     */
    private void loadUserRoutines(User user, Path userFolder) {
        Path dailyRoutineFile =
                userFolder.resolve(DAILY_ROUTINES_FILE_NAME);
        Path weeklyRoutineFile =
                userFolder.resolve(WEEKLY_ROUTINES_FILE_NAME);

        loadRoutineFile(
                dailyRoutineFile, user.getDailyRoutines(),
                RoutineType.DAILY
        );
        loadRoutineFile(
                weeklyRoutineFile, user.getWeeklyRoutines(),
                RoutineType.WEEKLY
        );
    }

    /**
     * Loads all day files for the given user.
     *
     * @param user User object to populate.
     * @param userFolder Folder containing the user's data.
     */
    private void loadUserDays(User user, Path userFolder) {
        Path daysFolder = userFolder.resolve(DAYS_FOLDER_NAME);

        if (Files.notExists(daysFolder)) {
            return;
        }

        try (Stream<Path> stream = Files.list(daysFolder)) {
            stream.filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(Path::getFileName))
                    .forEach(path -> addLoadedDay(user, path));
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load day files for "
                            + user.getName(), e
            );
        }
    }

    /**
     * Loads one day file and adds it to the user if valid.
     *
     * @param user User to update.
     * @param filePath Day file path.
     */
    private void addLoadedDay(User user, Path filePath) {
        Day day = loadDayFile(filePath);

        if (day != null) {
            user.addDay(day);
        }
    }

    /**
     * Ensures today's day record exists for the given user.
     *
     * @param user User to update.
     */
    private void ensureTodayExists(User user) {
        user.getOrCreateDay(LocalDate.now());
    }

    /**
     * Saves a user and all associated data.
     *
     * @param user User to save.
     */
    public void saveUser(User user) {
        Path userFolder = getUserFolder(user.getName());
        Path daysFolder = userFolder.resolve(DAYS_FOLDER_NAME);

        try {
            Files.createDirectories(userFolder);
            Files.createDirectories(daysFolder);

            writeProfileFile(userFolder, user);
            writeRoutineFiles(userFolder, user);
            writeDayFiles(daysFolder, user);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to save user " + user.getName(), e
            );
        }
    }

    /**
     * Writes the user's profile file.
     *
     * @param userFolder User folder.
     * @param user User to write.
     * @throws IOException If writing fails.
     */
    private void writeProfileFile(Path userFolder, User user) throws IOException {
        Path profileFile = userFolder.resolve(PROFILE_FILE_NAME);
        Files.writeString(profileFile, user.getName());
    }

    /**
     * Writes the user's routine files.
     *
     * @param userFolder User folder.
     * @param user User to write.
     * @throws IOException If writing fails.
     */
    private void writeRoutineFiles(Path userFolder, User user) throws IOException {
        Path dailyRoutineFile = userFolder.resolve(DAILY_ROUTINES_FILE_NAME);
        Path weeklyRoutineFile = userFolder.resolve(WEEKLY_ROUTINES_FILE_NAME);

        writeRoutineFile(dailyRoutineFile, user.getDailyRoutines());
        writeRoutineFile(weeklyRoutineFile, user.getWeeklyRoutines());
    }

    /**
     * Writes all day files for the user.
     *
     * @param daysFolder Days folder.
     * @param user User whose day files are written.
     * @throws IOException If writing fails.
     */
    private void writeDayFiles(Path daysFolder, User user) throws IOException {
        for (Day day : user.getDays()) {
            Path dayFile = daysFolder.resolve(
                    day.getDate() + ".txt"
            );
            saveDayFile(dayFile, day);
        }
    }

    /**
     * Loads routine tasks from a file.
     *
     * @param filePath Path to the routine file.
     * @param taskList Task list to populate.
     * @param type Routine type to assign.
     */
    private void loadRoutineFile(Path filePath, TaskList taskList, RoutineType type) {
        if (Files.notExists(filePath)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                addRoutineLine(taskList, type, line);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load routine file: " + filePath, e
            );
        }
    }

    /**
     * Adds one non-blank routine line to the task list.
     *
     * @param taskList Task list to update.
     * @param type Routine type to assign.
     * @param line Raw file line.
     */
    private void addRoutineLine(
            TaskList taskList, RoutineType type, String line
    ) {
        String trimmed = line.trim();

        if (!trimmed.isEmpty()) {
            taskList.addTask(new Task(trimmed, type));
        }
    }

    /**
     * Writes routine tasks to a file.
     *
     * @param filePath Path to write to.
     * @param taskList Task list to persist.
     * @throws IOException If writing fails.
     */
    private void writeRoutineFile(
            Path filePath, TaskList taskList
    ) throws IOException {
        List<String> lines = new ArrayList<>();

        for (Task task : taskList.getAllTasks()) {
            lines.add(task.getDescription());
        }

        Files.write(filePath, lines);
    }

    /**
     * Loads a single day file.
     *
     * @param filePath Path of the day file.
     * @return Loaded day, or null if invalid.
     */
    private Day loadDayFile(Path filePath) {
        try {
            LocalDate date = parseDayDate(filePath);
            Day day = new Day(date);
            List<String> lines = Files.readAllLines(filePath);

            applyDayFileLines(day, lines);
            return day;
        } catch (DateTimeParseException | IOException e) {
            System.err.println(
                    "Skipping invalid day file "
                            + filePath + ": " + e.getMessage()
            );
            return null;
        }
    }

    /**
     * Parses the date from a day file name.
     *
     * @param filePath Day file path.
     * @return Parsed date.
     */
    private LocalDate parseDayDate(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String dateText = fileName.replace(".txt", "");
        return LocalDate.parse(dateText);
    }

    /**
     * Applies all lines from a day file to the given day object.
     *
     * @param day Day object to populate.
     * @param lines Lines read from the file.
     */
    private void applyDayFileLines(Day day, List<String> lines) {
        String section = "";

        for (String line : lines) {
            if (isLogLine(line)) {
                day.setLog(readLogValue(line));
            } else if (isDailySectionHeader(line)) {
                section = DAILY_SECTION;
            } else if (isWeeklySectionHeader(line)) {
                section = WEEKLY_SECTION;
            } else if (!line.isBlank()) {
                applyTaskStatusLine(day, line, section);
            }
        }
    }

    /**
     * Returns whether the line is a log line.
     *
     * @param line Line to check.
     * @return True if the line starts with "log=".
     */
    private boolean isLogLine(String line) {
        return line.startsWith("log=");
    }

    /**
     * Returns whether the line is the daily section header.
     *
     * @param line Line to check.
     * @return True if the line is the daily section header.
     */
    private boolean isDailySectionHeader(String line) {
        return DAILY_SECTION_HEADER.equals(line);
    }

    /**
     * Returns whether the line is the weekly section header.
     *
     * @param line Line to check.
     * @return True if the line is the weekly section header.
     */
    private boolean isWeeklySectionHeader(String line) {
        return WEEKLY_SECTION_HEADER.equals(line);
    }

    /**
     * Extracts the log value from a log line.
     *
     * @param line Log line beginning with "log=".
     * @return Log text.
     */
    private String readLogValue(String line) {
        return line.substring(4);
    }

    /**
     * Applies one task-status line to the correct section of the day object.
     *
     * @param day Day object to update.
     * @param line Raw task-status line.
     * @param section Current section name.
     */
    private void applyTaskStatusLine(
            Day day, String line, String section
    ) {
        String[] parts = line.split("=", 2);

        if (parts.length != 2) {
            return;
        }

        String taskName = parts[0].trim();
        boolean completed = Boolean.parseBoolean(parts[1].trim());

        if (DAILY_SECTION.equals(section)) {
            day.setDailyCompleted(taskName, completed);
        } else if (WEEKLY_SECTION.equals(section)) {
            day.setWeeklyCompleted(taskName, completed);
        }
    }

    /**
     * Saves a single day file.
     *
     * @param filePath Path to write to.
     * @param day Day object to persist.
     * @throws IOException If writing fails.
     */
    private void saveDayFile(
            Path filePath, Day day
    ) throws IOException {
        List<String> lines = new ArrayList<>();

        lines.add("log=" + day.getLog());
        lines.add(DAILY_SECTION_HEADER);

        for (String task : day.getDailyStatus().keySet()) {
            boolean completed = day.getDailyStatus().get(task);
            lines.add(task + "=" + completed);
        }

        lines.add(WEEKLY_SECTION_HEADER);

        for (String task : day.getWeeklyStatus().keySet()) {
            boolean completed = day.getWeeklyStatus().get(task);
            lines.add(task + "=" + completed);
        }

        Files.write(filePath, lines);
    }

    /**
     * Deletes the data folder for the specified user.
     *
     * @param userName Name of the user.
     * @throws IOException If deletion fails.
     */
    private void deleteUserFolder(
            String userName
    ) throws IOException {
        Path userFolder = getUserFolder(userName);

        try (Stream<Path> stream = Files.walk(userFolder)) {
            deletePathsInReverseOrder(stream);
        }
    }

    /**
     * Deletes all given paths from deepest to shallowest.
     *
     * @param stream Stream of paths to delete.
     */
    private void deletePathsInReverseOrder(Stream<Path> stream) {
        stream.sorted(Comparator.reverseOrder())
                .forEach(this::deletePath);
    }

    /**
     * Deletes one path, wrapping checked exceptions.
     *
     * @param path Path to delete.
     */
    private void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the folder path for a user.
     *
     * @param userName User name.
     * @return User folder path.
     */
    private Path getUserFolder(String userName) {
        return usersRoot.resolve(sanitizeName(userName));
    }

    /**
     * Reads display name of a user.
     *
     * @param userFolder User folder.
     * @return Display name of the user.
     */
    private String readUserDisplayName(Path userFolder) {
        Path profileFile = userFolder.resolve(PROFILE_FILE_NAME);

        if (Files.exists(profileFile)) {
            try {
                return Files.readString(profileFile).trim();
            } catch (IOException ignored) {
                // Fall back to folder name.
            }
        }

        return userFolder.getFileName().toString().replace("_", " ");
    }

    /**
     * Sanitizes user name for file system usage.
     *
     * @param name Raw user name.
     * @return Sanitized user name.
     */
    private String sanitizeName(String name) {
        return name.trim().replace(" ", "_");
    }

    /**
     * Normalizes a user-entered name.
     *
     * @param name Raw input name.
     * @return Trimmed name, or an empty string if null.
     */
    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }

        return name.trim();
    }
}
