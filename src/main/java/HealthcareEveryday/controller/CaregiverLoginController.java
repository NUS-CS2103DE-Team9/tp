package HealthcareEveryday.controller;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.AuthService;
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
    private AuthService authService;

    /**
     * Sets the main application reference for scene switching and service access.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.authService = mainApp.getAuthService();
    }

    /**
     * Handles the caregiver login attempt.
     */
    @FXML
    private void handleLogin() {
        try {
            boolean valid = authService.validateCaregiverPassword(passwordField.getText());

            if (valid) {
                handleSuccessfulLogin();
            } else {
                showWrongPassword();
            }
        } catch (RuntimeException e) {
            showSystemError();
        }
    }

    /**
     * Handles a successful login.
     */
    private void handleSuccessfulLogin() {
        errorLabel.setText("");
        mainApp.showCaregiverMenuScene();
    }

    /**
     * Shows the wrong-password message.
     */
    private void showWrongPassword() {
        errorLabel.setText("Wrong password, please try again.");
    }

    /**
     * Shows a system-error message.
     */
    private void showSystemError() {
        errorLabel.setText("System error. Please try again later.");
    }

    /**
     * Returns the user to the login scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showLoginScene();
    }
}
