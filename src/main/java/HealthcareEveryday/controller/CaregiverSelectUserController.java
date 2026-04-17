package HealthcareEveryday.controller;

import java.util.List;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for choosing which senior's routines to edit.
 */
public class CaregiverSelectUserController {
    private static final double USER_BUTTON_WIDTH = 260;

    @FXML
    private Label titleLabel;

    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private AuthService authService;

    /**
     * Sets the main application reference and loads users.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.authService = mainApp.getAuthService();
        titleLabel.setText("Whose tasks are we modifying?");
        loadUsers();
    }

    /**
     * Loads the list of users and displays them as buttons.
     */
    private void loadUsers() {
        userContainer.getChildren().clear();

        try {
            List<String> users = authService.getSeniorNames();

            for (String user : users) {
                Button button = createUserButton(user);
                userContainer.getChildren().add(button);
            }
        } catch (RuntimeException e) {
            showLoadFailedAlert();
        }
    }

    /**
     * Creates a button for one senior user.
     *
     * @param userName Name of the user.
     * @return Configured user button.
     */
    private Button createUserButton(String userName) {
        Button button = new Button(userName);
        button.setPrefWidth(USER_BUTTON_WIDTH);
        button.getStyleClass().add("choice");
        button.setOnAction(e -> mainApp.showEditRoutineScene(userName));
        return button;
    }

    /**
     * Shows an alert when user loading fails.
     */
    private void showLoadFailedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load failed");
        alert.setHeaderText("Unable to load users");
        alert.setContentText("Please check your data files and try again.");
        alert.showAndWait();
    }

    /**
     * Returns the user to the caregiver menu scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }
}
