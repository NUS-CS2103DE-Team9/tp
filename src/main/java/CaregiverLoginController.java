import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * Controller for the caregiver login view.
 */
public class CaregiverLoginController {
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private MainApp mainApp;

    /**
     * Sets the main application reference for scene switching and storage access.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Handles the caregiver login attempt.
     */
    @FXML
    private void handleLogin() {
        boolean valid = mainApp.getStorage().validateCaregiverPassword(passwordField.getText());
        if (valid) {
            errorLabel.setText("");
            mainApp.showCaregiverMenuScene();
        } else {
            errorLabel.setText("Wrong password, please try again.");
        }
    }

    /**
     * Returns the user to the login scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showLoginScene();
    }
}
