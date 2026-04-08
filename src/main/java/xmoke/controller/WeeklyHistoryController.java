package xmoke.controller;

import xmoke.MainApp;
import xmoke.service.HistoryService;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the weekly history view.
 */
public class WeeklyHistoryController {
    @FXML
    private Label titleLabel;

    @FXML
    private Label periodLabel;

    @FXML
    private VBox contentContainer;

    private MainApp mainApp;
    private HistoryService historyService;
    private String userName;

    /**
     * Sets the main application reference and service reference.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.historyService = mainApp.getHistoryService();
    }

    /**
     * Sets the user name and loads the weekly history.
     *
     * @param userName Name of the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
        loadWeeklyHistory();
    }

    /**
     * Loads and displays the weekly task completion history for the user.
     */
    private void loadWeeklyHistory() {
        HistoryService.WeeklyUserHistory weeklyHistory = historyService.getWeeklyHistory(userName);

        titleLabel.setText(weeklyHistory.getUserName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        periodLabel.setText(
                "Time period: "
                        + weeklyHistory.getStartDate().format(formatter)
                        + " to "
                        + weeklyHistory.getEndDate().format(formatter)
        );

        contentContainer.getChildren().clear();

        for (HistoryService.DailyTaskWeeklyRecord record : weeklyHistory.getDailyRecords()) {
            VBox block = new VBox(3);
            block.getStyleClass().add("weekly-block");
            block.getChildren().add(new Label(record.getTaskName() + ": " + record.getCompletedCount() + "/7"));
            block.getChildren().add(
                    new Label("Missed on: "
                            + (record.getMissedDays().isEmpty() ? "-" : String.join(", ", record.getMissedDays())))
            );
            contentContainer.getChildren().add(block);
        }

        for (HistoryService.WeeklyTaskWeeklyRecord record : weeklyHistory.getWeeklyRecords()) {
            VBox block = new VBox(3);
            block.getStyleClass().add("weekly-block");
            block.getChildren().add(
                    new Label(record.getTaskName() + ": "
                            + (record.isDoneThisWeek() ? "Done this week" : "Not done this week"))
            );
            block.getChildren().add(
                    new Label("Marked on: "
                            + (record.getDoneDays().isEmpty() ? "-" : String.join(", ", record.getDoneDays())))
            );
            contentContainer.getChildren().add(block);
        }
    }

    /**
     * Returns the user to the history user selection scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showHistorySelectUserScene("week");
    }
}
