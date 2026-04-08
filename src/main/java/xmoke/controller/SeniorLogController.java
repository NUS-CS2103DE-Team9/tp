package xmoke.controller;

import xmoke.MainApp;
import xmoke.service.LogService;

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
        logArea.setText(logService.getTodayLog(userName));
        statusLabel.setText("");
    }

    /**
     * Handles submitting and saving today's log.
     */
    @FXML
    private void handleSubmit() {
        logService.saveTodayLog(userName, logArea.getText());
        statusLabel.setText("Today's record saved.");
        mainApp.showLoginScene();
    }

    /**
     * Returns the user to the senior tasks scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showSeniorTasksScene(userName);
    }
}
