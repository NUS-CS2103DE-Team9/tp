package HealthcareEveryday.controller;

import java.time.format.DateTimeFormatter;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.HistoryService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the weekly history view.
 */
public class WeeklyHistoryController {
    private static final int BLOCK_SPACING = 3;

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

        showHeader(weeklyHistory);

        contentContainer.getChildren().clear();
        addDailyRecordBlocks(weeklyHistory);
        addWeeklyRecordBlocks(weeklyHistory);
    }

    /**
     * Displays the header information for the current weekly history.
     *
     * @param weeklyHistory Weekly history data.
     */
    private void showHeader(HistoryService.WeeklyUserHistory weeklyHistory) {
        titleLabel.setText(weeklyHistory.getUserName());
        periodLabel.setText(buildPeriodText(weeklyHistory));
    }

    /**
     * Builds the period text shown below the title.
     *
     * @param weeklyHistory Weekly history data.
     * @return Formatted period text.
     */
    private String buildPeriodText(HistoryService.WeeklyUserHistory weeklyHistory) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String startDateText = weeklyHistory.getStartDate().format(formatter);
        String endDateText = weeklyHistory.getEndDate().format(formatter);

        return "Time period: " + startDateText + " to " + endDateText;
    }

    /**
     * Adds all daily task weekly record blocks to the content container.
     *
     * @param weeklyHistory Weekly history data.
     */
    private void addDailyRecordBlocks(HistoryService.WeeklyUserHistory weeklyHistory) {
        for (HistoryService.DailyTaskWeeklyRecord record : weeklyHistory.getDailyRecords()) {
            VBox block = createDailyRecordBlock(record);
            contentContainer.getChildren().add(block);
        }
    }

    /**
     * Adds all weekly task weekly record blocks to the content container.
     *
     * @param weeklyHistory Weekly history data.
     */
    private void addWeeklyRecordBlocks(HistoryService.WeeklyUserHistory weeklyHistory) {
        for (HistoryService.WeeklyTaskWeeklyRecord record : weeklyHistory.getWeeklyRecords()) {
            VBox block = createWeeklyRecordBlock(record);
            contentContainer.getChildren().add(block);
        }
    }

    /**
     * Creates a display block for one daily task weekly record.
     *
     * @param record Daily task weekly record.
     * @return Configured display block.
     */
    private VBox createDailyRecordBlock(HistoryService.DailyTaskWeeklyRecord record) {
        VBox block = new VBox(BLOCK_SPACING);
        block.getStyleClass().add("weekly-block");

        String completedText = record.getTaskName() + ": " + record.getCompletedCount() + "/7";
        String missedText = buildMissedDaysText(record);

        block.getChildren().add(new Label(completedText));
        block.getChildren().add(new Label(missedText));

        return block;
    }

    /**
     * Creates a display block for one weekly task weekly record.
     *
     * @param record Weekly task weekly record.
     * @return Configured display block.
     */
    private VBox createWeeklyRecordBlock(HistoryService.WeeklyTaskWeeklyRecord record) {
        VBox block = new VBox(BLOCK_SPACING);
        block.getStyleClass().add("weekly-block");

        String statusText = buildWeeklyStatusText(record);
        String markedText = buildMarkedDaysText(record);

        block.getChildren().add(new Label(statusText));
        block.getChildren().add(new Label(markedText));

        return block;
    }

    /**
     * Builds the missed-days text for a daily record.
     *
     * @param record Daily task weekly record.
     * @return Formatted missed-days text.
     */
    private String buildMissedDaysText(HistoryService.DailyTaskWeeklyRecord record) {
        if (record.getMissedDays().isEmpty()) {
            return "Missed on: -";
        }
        return "Missed on: " + String.join(", ", record.getMissedDays());
    }

    /**
     * Builds the status text for a weekly record.
     *
     * @param record Weekly task weekly record.
     * @return Formatted weekly status text.
     */
    private String buildWeeklyStatusText(HistoryService.WeeklyTaskWeeklyRecord record) {
        if (record.isDoneThisWeek()) {
            return record.getTaskName() + ": Done this week";
        } else {
            return record.getTaskName() + ": Not done this week";
        }
    }

    /**
     * Builds the marked-days text for a weekly record.
     *
     * @param record Weekly task weekly record.
     * @return Formatted marked-days text.
     */
    private String buildMarkedDaysText(HistoryService.WeeklyTaskWeeklyRecord record) {
        if (record.getDoneDays().isEmpty()) {
            return "Marked on: -";
        }
        return "Marked on: " + String.join(", ", record.getDoneDays());
    }

    /**
     * Returns the user to the history user selection scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showHistorySelectUserScene("week");
    }
}
