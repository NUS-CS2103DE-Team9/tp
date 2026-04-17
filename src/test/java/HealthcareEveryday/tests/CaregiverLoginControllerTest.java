package HealthcareEveryday.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import HealthcareEveryday.controller.CaregiverLoginController;
import HealthcareEveryday.service.AuthService;
import HealthcareEveryday.storage.Storage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

class CaregiverLoginControllerTest extends FxTestUtils {

    @TempDir
    Path tempDir;

    @Test
    void handleLoginShowsWrongPasswordMessageWhenCredentialsAreInvalid() {
        // Purpose: verify controller displays invalid-password message for failed login attempts.
        CaregiverLoginController controller = new CaregiverLoginController();
        AuthService authService = new AuthService(new Storage(tempDir.resolve("data")));
        PasswordField passwordField = new PasswordField();
        Label errorLabel = new Label();

        passwordField.setText("not-correct");
        setField(controller, "authService", authService);
        setField(controller, "passwordField", passwordField);
        setField(controller, "errorLabel", errorLabel);

        runOnFxThreadAndWait(() -> invokePrivate(controller, "handleLogin"));

        assertEquals("Wrong password, please try again.", errorLabel.getText());
    }

    @Test
    void handleLoginShowsSystemErrorWhenAuthServiceThrows() {
        // Purpose: verify controller surfaces system-error message when auth validation crashes.
        CaregiverLoginController controller = new CaregiverLoginController();
        AuthService throwingAuthService = new AuthService(new Storage(tempDir.resolve("data"))) {
            @Override
            public boolean validateCaregiverPassword(String password) {
                throw new RuntimeException("forced failure");
            }
        };
        PasswordField passwordField = new PasswordField();
        Label errorLabel = new Label();

        passwordField.setText("anything");
        setField(controller, "authService", throwingAuthService);
        setField(controller, "passwordField", passwordField);
        setField(controller, "errorLabel", errorLabel);

        runOnFxThreadAndWait(() -> invokePrivate(controller, "handleLogin"));

        assertEquals("System error. Please try again later.", errorLabel.getText());
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void invokePrivate(Object target, String methodName) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
