package HealthcareEveryday.controller;

import java.util.List;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.service.HistoryService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller for the today's history view.
 */
public class TodayHistoryController {
    private static final int USER_BOX_SPACING = 8;
    private static final int USER_ROW_SPACING = 40;

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
            HBox row = createUserHistoryRow(userHistory);
            usersContainer.getChildren().add(row);
        }
    }

    /**
     * Creates one display row for a user's history card.
     *
     * @param userHistory Today's history for one user.
     * @return Configured row containing the user card.
     */
    private HBox createUserHistoryRow(HistoryService.TodayUserHistory userHistory) {
        HBox row = new HBox(USER_ROW_SPACING);
        VBox userCard = createUserHistoryCard(userHistory);
        row.getChildren().add(userCard);
        return row;
    }

    /**
     * Creates one user history card.
     *
     * @param userHistory Today's history for one user.
     * @return Configured user history card.
     */
    private VBox createUserHistoryCard(HistoryService.TodayUserHistory userHistory) {
        VBox userBox = new VBox(USER_BOX_SPACING);
        userBox.getStyleClass().add("history-card");

        Label nameLabel = new Label(userHistory.getUserName());
        nameLabel.getStyleClass().add("history-user-name");
        userBox.getChildren().add(nameLabel);

        addTaskSection(userBox, "Daily", userHistory.getDailyTasks());
        addTaskSection(userBox, "Weekly", userHistory.getWeeklyTasks());

        return userBox;
    }

    /**
     * Adds one task section with heading and task labels.
     *
     * @param container Container to add the section into.
     * @param headingText Section heading text.
     * @param taskStatuses Task statuses to display.
     */
    private void addTaskSection(
            VBox container,
            String headingText,
            List<HistoryService.TaskStatus> taskStatuses
    ) {
        Label heading = new Label(headingText);
        heading.getStyleClass().add("section-heading");
        container.getChildren().add(heading);

        for (HistoryService.TaskStatus taskStatus : taskStatuses) {
            Label taskLabel = createTaskLabel(taskStatus);
            container.getChildren().add(taskLabel);
        }
    }

    /**
     * Creates one label for a task status.
     *
     * @param taskStatus Task status to display.
     * @return Configured task label.
     */
    private Label createTaskLabel(HistoryService.TaskStatus taskStatus) {
        String mark = buildTaskMark(taskStatus);
        return new Label(mark + taskStatus.getTaskName());
    }

    /**
     * Builds the display mark for a task status.
     *
     * @param taskStatus Task status to check.
     * @return Display mark for the task.
     */
    private String buildTaskMark(HistoryService.TaskStatus taskStatus) {
        if (taskStatus.isCompleted()) {
            return "[✓] ";
        } else {
            return "[ ] ";
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
