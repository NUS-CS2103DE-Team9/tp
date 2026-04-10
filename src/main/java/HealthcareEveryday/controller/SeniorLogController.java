package HealthcareEveryday.controller;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.LogService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Controller for the senior daily log view.
 */
public class SeniorLogController {
    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label userLabel;

    @FXML
    private TextArea logArea;

    @FXML
    private Label statusLabel;

    private MainApp mainApp;
    private LogService logService;
    private String userName;

    /**
     * Sets the main application reference and service reference.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.logService = mainApp.getLogService();
    }

    /**
     * Sets the user name and loads today's log data.
     *
     * @param userName Name of the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
        pageTitleLabel.setText("Daily Log");
        userLabel.setText(userName);
        try {
            logArea.setText(logService.getTodayLog(userName));
            statusLabel.setText("");
        } catch (RuntimeException e) {
            logArea.setText("");
            statusLabel.setText("Unable to load today's log. Please try again.");
        }
    }

    /**
     * Handles submitting and saving today's log.
     */
    @FXML
    private void handleSubmit() {
        try {
            logService.saveTodayLog(userName, logArea.getText());
            statusLabel.setText("Today's record saved.");
            mainApp.showLoginScene();
        } catch (RuntimeException e) {
            statusLabel.setText("Save failed. Please try again.");
        }
    }

    /**
     * Returns the user to the senior tasks scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showSeniorTasksScene(userName);
    }
}
