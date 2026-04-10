package HealthcareEveryday.tests;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UiViewTest extends FxTestUtils {

    @Test
    void loginViewFxmlLoadsAndContainsCaregiverButton() {
        // Purpose: verify LoginView FXML can load and exposes expected caregiver action button.
        AtomicReference<Parent> rootRef = new AtomicReference<>();
        AtomicReference<Button> buttonRef = new AtomicReference<>();

        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
                Parent root = loader.load();
                rootRef.set(root);
                buttonRef.set((Button) root.lookup(".button"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertNotNull(rootRef.get());
        assertNotNull(buttonRef.get());
        assertEquals("Caregiver", buttonRef.get().getText());
    }

    @Test
    void caregiverLoginViewFxmlLoadsAndContainsPasswordField() {
        // Purpose: verify CaregiverLoginView FXML can load and wire required password input control.
        AtomicReference<Parent> rootRef = new AtomicReference<>();
        AtomicReference<PasswordField> passwordFieldRef = new AtomicReference<>();

        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CaregiverLoginView.fxml"));
                Parent root = loader.load();
                rootRef.set(root);
                passwordFieldRef.set((PasswordField) root.lookup(".password-field"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertNotNull(rootRef.get());
        assertNotNull(passwordFieldRef.get());
    }
}
