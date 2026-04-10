package HealthcareEveryday.tests;

import HealthcareEveryday.service.AuthService;
import HealthcareEveryday.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void validateCaregiverPasswordAcceptsDefaultAndRejectsWrongValue() {
        // Purpose: cover positive and negative password validation through service layer.
        AuthService authService = new AuthService(new Storage(tempDir.resolve("data")));

        assertTrue(authService.validateCaregiverPassword("caregiver"));
        assertFalse(authService.validateCaregiverPassword("wrong-password"));
    }

    @Test
    void changeCaregiverPasswordSucceedsWithCorrectOldPassword() {
        // Purpose: verify happy-path password change and subsequent validation.
        AuthService authService = new AuthService(new Storage(tempDir.resolve("data")));

        assertTrue(authService.changeCaregiverPassword("caregiver", "new-pass"));
        assertTrue(authService.validateCaregiverPassword("new-pass"));
        assertFalse(authService.validateCaregiverPassword("caregiver"));
    }

    @Test
    void changeCaregiverPasswordFailsWithWrongOldOrBlankNewPassword() {
        // Purpose: verify negative paths for wrong old password and invalid new password.
        AuthService authService = new AuthService(new Storage(tempDir.resolve("data")));

        assertFalse(authService.changeCaregiverPassword("wrong-old", "new-pass"));
        assertFalse(authService.changeCaregiverPassword("caregiver", "  "));
    }

    @Test
    void addUserAndGetSeniorNamesExposeCreatedUser() {
        // Purpose: verify AuthService user management flow through add and list operations.
        AuthService authService = new AuthService(new Storage(tempDir.resolve("data")));

        assertTrue(authService.addUser("Senior Hotel"));

        assertTrue(authService.getSeniorNames().contains("Senior Hotel"));
    }
}
