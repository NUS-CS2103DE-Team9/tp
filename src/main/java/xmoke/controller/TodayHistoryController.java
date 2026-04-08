package xmoke.controller;

import xmoke.MainApp;
import xmoke.service.HistoryService;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for the today's history view.
 */
public class TodayHistoryController {
    @FXML
    private VBox usersContainer;

    private MainApp mainApp;
    private HistoryService historyService;

    /**
     * Sets the main application reference and service reference.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.historyService = mainApp.getHistoryService();
        loadTodayHistory();
    }

    /**
     * Loads and displays today's task completion history for all users.
     */
    private void loadTodayHistory() {
        usersContainer.getChildren().clear();
        List<HistoryService.TodayUserHistory> users = historyService.getTodayHistoryForAllUsers();

        for (HistoryService.TodayUserHistory userHistory : users) {
            VBox userBox = new VBox(8);
            userBox.getStyleClass().add("history-card");

            Label nameLabel = new Label(userHistory.getUserName());
            nameLabel.getStyleClass().add("history-user-name");
            userBox.getChildren().add(nameLabel);

            Label dailyHeading = new Label("Daily");
            dailyHeading.getStyleClass().add("section-heading");
            userBox.getChildren().add(dailyHeading);

            for (HistoryService.TaskStatus taskStatus : userHistory.getDailyTasks()) {
                String mark = taskStatus.isCompleted() ? "[✓] " : "[ ] ";
                userBox.getChildren().add(new Label(mark + taskStatus.getTaskName()));
            }

            Label weeklyHeading = new Label("Weekly");
            weeklyHeading.getStyleClass().add("section-heading");
            userBox.getChildren().add(weeklyHeading);

            for (HistoryService.TaskStatus taskStatus : userHistory.getWeeklyTasks()) {
                String mark = taskStatus.isCompleted() ? "[✓] " : "[ ] ";
                userBox.getChildren().add(new Label(mark + taskStatus.getTaskName()));
            }

            HBox row = new HBox(40);
            row.getChildren().add(userBox);
            usersContainer.getChildren().add(row);
        }
    }

    /**
     * Returns the user to the history period selection scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showHistoryPeriodScene();
    }
}
