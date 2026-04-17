package HealthcareEveryday.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import HealthcareEveryday.model.RoutineType;
import HealthcareEveryday.model.User;
import HealthcareEveryday.service.RoutineService;
import HealthcareEveryday.storage.Storage;

class RoutineServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void addRoutineAddsNewTaskButRejectsBlankAndDuplicate() {
        // Purpose: cover routine add success and key validation failures.
        RoutineService service = new RoutineService(new Storage(tempDir.resolve("data")));

        assertFalse(service.addRoutine("Senior Echo", " ", RoutineType.DAILY));
        assertTrue(service.addRoutine("Senior Echo", "Take medicine", RoutineType.DAILY));
        assertFalse(service.addRoutine("Senior Echo", "Take medicine", RoutineType.DAILY));
    }

    @Test
    void removeRoutineReturnsTrueWhenTaskExistsAndFalseOtherwise() {
        // Purpose: verify both positive and negative remove behavior.
        RoutineService service = new RoutineService(new Storage(tempDir.resolve("data")));
        service.addRoutine("Senior Foxtrot", "Walk 10 minutes", RoutineType.WEEKLY);

        assertTrue(service.removeRoutine("Senior Foxtrot", "Walk 10 minutes", RoutineType.WEEKLY));
        assertFalse(service.removeRoutine("Senior Foxtrot", "Walk 10 minutes", RoutineType.WEEKLY));
    }

    @Test
    void setDailyCompletedPersistsCompletionState() {
        // Purpose: ensure completion updates are saved and visible after reload.
        Storage storage = new Storage(tempDir.resolve("data"));
        RoutineService service = new RoutineService(storage);
        service.addRoutine("Senior Golf", "Drink water", RoutineType.DAILY);

        service.setDailyCompleted("Senior Golf", "Drink water", true);
        User loaded = storage.loadUser("Senior Golf");

        assertTrue(loaded.getOrCreateDay(LocalDate.now()).isDailyCompleted("Drink water"));
    }

    @Test
    void setWeeklyCompletedPersistsCompletionState() {
        // Purpose: verify weekly completion updates are persisted like daily updates.
        Storage storage = new Storage(tempDir.resolve("data"));
        RoutineService service = new RoutineService(storage);
        service.addRoutine("Senior India", "Stretching", RoutineType.WEEKLY);

        service.setWeeklyCompleted("Senior India", "Stretching", true);
        User loaded = storage.loadUser("Senior India");

        assertTrue(loaded.getOrCreateDay(LocalDate.now()).isWeeklyCompleted("Stretching"));
    }
}
