package HealthcareEveryday.controller;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.AuthService;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

/**
 * Controller for the login view.
 */
public class LoginController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private AuthService authService;

    /**
     * Sets the main application reference and loads the users.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.authService = mainApp.getAuthService();
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
                Button button = new Button(user);
                button.setPrefWidth(260);
                button.getStyleClass().add("choice");
                button.setOnAction(e -> mainApp.showSeniorTasksScene(user));
                userContainer.getChildren().add(button);
            }
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load failed");
            alert.setHeaderText("Unable to load users");
            alert.setContentText("Please check your data files and try again.");
            alert.showAndWait();
        }
    }

    /**
     * Opens the caregiver login scene.
     */
    @FXML
    private void handleCaregiver() {
        mainApp.showCaregiverLoginScene();
    }
}
