package HealthcareEveryday.controller;

import java.util.List;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Controller for the history user selection view.
 */
public class HistorySelectUserController {
    private static final double USER_BUTTON_WIDTH = 260;
    private static final String WEEK_PERIOD = "week";

    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private AuthService authService;
    private String period;

    /**
     * Sets the main application reference for scene switching.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.authService = mainApp.getAuthService();
    }

    /**
     * Sets the selected history period and loads the users.
     *
     * @param period Selected period.
     */
    public void setPeriod(String period) {
        this.period = period;
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
        button.setOnAction(e -> openSelectedHistory(userName));
        return button;
    }

    /**
     * Opens the selected history view for the given user.
     *
     * @param userName Name of the user.
     */
    private void openSelectedHistory(String userName) {
        if (WEEK_PERIOD.equals(period)) {
            mainApp.showWeeklyHistoryScene(userName);
        }
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
     * Returns the user to the history period selection scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showHistoryPeriodScene();
    }
}
