package HealthcareEveryday.tests;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.RoutineType;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.User;
import HealthcareEveryday.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    void constructorCreatesBaseFoldersAndDefaultCaregiverPassword() throws Exception {
        // Purpose: ensure Storage bootstrap creates required folders and default credential file.
        Storage storage = new Storage(tempDir.resolve("data"));

        Path users = tempDir.resolve("data").resolve("users");
        Path app = tempDir.resolve("data").resolve("app");
        Path caregiverFile = app.resolve("caregiver.txt");

        assertTrue(Files.isDirectory(users));
        assertTrue(Files.isDirectory(app));
        assertEquals("caregiver", Files.readString(caregiverFile).trim());
        assertTrue(storage.validateCaregiverPassword("caregiver"));
    }

    @Test
    void addUserPersistsProfileAndAppearsInUserList() throws Exception {
        // Purpose: verify positive path for user creation and persistence metadata.
        Storage storage = new Storage(tempDir.resolve("data"));

        assertTrue(storage.addUser("Senior Alpha"));
        List<String> names = storage.listSeniorNames();

        Path profile = tempDir.resolve("data/users/Senior_Alpha/profile.txt");
        assertTrue(Files.exists(profile));
        assertTrue(names.contains("Senior Alpha"));
    }

    @Test
    void addUserRejectsBlankOrDuplicateNames() {
        // Purpose: verify negative validation paths for invalid and duplicate names.
        Storage storage = new Storage(tempDir.resolve("data"));

        assertFalse(storage.addUser(" "));
        assertTrue(storage.addUser("Senior Beta"));
        assertFalse(storage.addUser("Senior Beta"));
    }

    @Test
    void loadUserSkipsInvalidDayFilesWithoutCrashing() throws Exception {
        // Purpose: ensure malformed day filenames are safely ignored during load.
        Storage storage = new Storage(tempDir.resolve("data"));
        storage.addUser("Senior Gamma");

        Path userDays = tempDir.resolve("data/users/Senior_Gamma/days");
        Files.createDirectories(userDays);
        Files.writeString(userDays.resolve("not-a-date.txt"), "log=bad");

        User loaded = storage.loadUser("Senior Gamma");
        boolean containsInvalidDay = loaded.getDays().stream()
                .map(Day::getDate)
                .anyMatch(date -> "not-a-date".equals(date.toString()));

        assertFalse(containsInvalidDay);
        assertTrue(loaded.getDays().stream().anyMatch(day -> day.getDate().equals(LocalDate.now())));
    }

    @Test
    void saveAndLoadUserRoundTripsRoutineAndCompletionState() {
        // Purpose: verify persisted routines and completion flags survive save/load cycle.
        Storage storage = new Storage(tempDir.resolve("data"));
        User user = new User("Senior Delta");
        user.getDailyRoutines().addTask(new Task("Drink water", RoutineType.DAILY));
        Day today = user.getOrCreateDay(LocalDate.now());
        today.setDailyCompleted("Drink water", true);

        storage.saveUser(user);
        User loaded = storage.loadUser("Senior Delta");

        assertTrue(loaded.getDailyRoutines().containsDescription("Drink water"));
        assertTrue(loaded.getOrCreateDay(LocalDate.now()).isDailyCompleted("Drink water"));
    }

    @Test
    void listSeniorNamesReturnsCaseInsensitiveSortedNames() {
        // Purpose: verify user names are returned in a stable case-insensitive sort order.
        Storage storage = new Storage(tempDir.resolve("data"));
        storage.addUser("zeta");
        storage.addUser("Alpha");
        storage.addUser("bravo");

        List<String> names = storage.listSeniorNames();

        assertEquals(List.of("Alpha", "bravo", "zeta"), names);
    }
}
