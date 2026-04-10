package HealthcareEveryday.controller;

import HealthcareEveryday.MainApp;
import javafx.fxml.FXML;

/**
 * Controller for the history period selection view.
 */
public class HistoryPeriodController {
    private MainApp mainApp;

    /**
     * Sets the main application reference for scene switching.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Handles selecting today's history view.
     */
    @FXML
    private void handleToday() {
        mainApp.showTodayHistoryScene();
    }

    /**
     * Handles selecting the past week history view.
     */
    @FXML
    private void handlePastWeek() {
        mainApp.showHistorySelectUserScene("week");
    }

    /**
     * Returns the user to the caregiver menu scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }
}
