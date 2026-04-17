package HealthcareEveryday.controller;

import java.util.List;
import java.util.Optional;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the delete user view.
 */
public class DeleteUserController {
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
        titleLabel.setText("Which user do you want to delete?");
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
                button.setOnAction(e -> handleDelete(user));
                userContainer.getChildren().add(button);
            }

            if (users.isEmpty()) {
                Label emptyLabel = new Label("No users available.");
                userContainer.getChildren().add(emptyLabel);
            }
        } catch (RuntimeException e) {
            showError("Load failed", "Unable to load users.");
        }
    }

    /**
     * Handles deleting the selected user.
     *
     * @param userName Name of the selected user.
     */
    private void handleDelete(String userName) {
        if (!isDeleteConfirmed(userName)) {
            return;
        }

        boolean deleted = authService.deleteUser(userName);

        if (deleted) {
            showInfo(
                    "Success",
                    "User \"" + userName + "\" has been deleted."
            );
            loadUsers();
        } else {
            showError(
                    "Delete failed",
                    "Unable to delete user \"" + userName + "\"."
            );
        }
    }

    /**
     * Returns whether deletion is confirmed.
     *
     * @param userName Name of the user to delete.
     * @return True if confirmed.
     */
    private boolean isDeleteConfirmed(String userName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(
                "Delete user \"" + userName + "\"?"
        );
        alert.setContentText(
                "This will permanently remove the user and all saved data."
        );

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Returns the user to the caregiver menu scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }

    /**
     * Shows an information alert.
     *
     * @param title Alert title.
     * @param message Alert message.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert.
     *
     * @param title Alert title.
     * @param message Alert message.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
