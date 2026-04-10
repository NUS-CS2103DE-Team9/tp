package HealthcareEveryday.controller;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.AuthService;
import HealthcareEveryday.service.SummaryService;

import java.nio.file.Path;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Controller for the summary generation user selection view.
 */
public class GenerateSummarySelectUserController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private AuthService authService;
    private SummaryService summaryService;

    /**
     * Sets the main application reference and service references.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.authService = mainApp.getAuthService();
        this.summaryService = mainApp.getSummaryService();
        loadUsers();
    }

    /**
     * Loads the list of users and displays them as buttons.
     */
    private void loadUsers() {
        userContainer.getChildren().clear();
        try {
            List<String> users = authService.getSeniorNames();
            for (String userName : users) {
                Button button = new Button(userName);
                button.setPrefWidth(260);
                button.getStyleClass().add("choice");
                button.setOnAction(e -> generateSummary(userName));
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
     * Generates a monthly summary report for the specified user.
     *
     * @param userName Name of the user.
     */
    private void generateSummary(String userName) {
        try {
            Path reportPath = summaryService.generateMonthlySummary(userName);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Generated");
            alert.setHeaderText(null);
            alert.setContentText("Report has been generated for:\n"
                    + userName
                    + "\n\nPlease check:\n"
                    + reportPath);
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Generation Failed");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Returns the user to the caregiver menu scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }
}
