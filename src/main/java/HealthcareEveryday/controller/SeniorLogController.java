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
        setPageText();
        loadTodayLog();
    }

    /**
     * Sets the page title and user label.
     */
    private void setPageText() {
        pageTitleLabel.setText("Daily Log");
        userLabel.setText(userName);
    }

    /**
     * Loads today's log data into the text area.
     */
    private void loadTodayLog() {
        try {
            String todayLog = logService.getTodayLog(userName);
            showLoadedLog(todayLog);
        } catch (RuntimeException e) {
            showLoadFailure();
        }
    }

    /**
     * Displays a successfully loaded log.
     *
     * @param logText Log text to display.
     */
    private void showLoadedLog(String logText) {
        logArea.setText(logText);
        statusLabel.setText("");
    }

    /**
     * Displays a load-failure message.
     */
    private void showLoadFailure() {
        logArea.setText("");
        statusLabel.setText("Unable to load today's log. Please try again.");
    }

    /**
     * Handles submitting and saving today's log.
     */
    @FXML
    private void handleSubmit() {
        try {
            logService.saveTodayLog(userName, logArea.getText());
            showSaveSuccess();
            mainApp.showLoginScene();
        } catch (RuntimeException e) {
            showSaveFailure();
        }
    }

    /**
     * Displays a successful save message.
     */
    private void showSaveSuccess() {
        statusLabel.setText("Today's record saved.");
    }

    /**
     * Displays a save-failure message.
     */
    private void showSaveFailure() {
        statusLabel.setText("Save failed. Please try again.");
    }

    /**
     * Returns the user to the senior tasks scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showSeniorTasksScene(userName);
    }
}
