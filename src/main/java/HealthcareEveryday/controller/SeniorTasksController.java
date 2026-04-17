package HealthcareEveryday.controller;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.RoutineType;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.TaskList;
import HealthcareEveryday.service.RoutineService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the senior tasks view.
 */
public class SeniorTasksController {
    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label userLabel;

    @FXML
    private VBox dailyContainer;

    @FXML
    private VBox weeklyContainer;

    @FXML
    private Label dailyEmptyLabel;

    @FXML
    private Label weeklyEmptyLabel;

    @FXML
    private Label statusLabel;

    private MainApp mainApp;
    private RoutineService routineService;
    private String userName;

    /**
     * Sets the main application reference and service reference.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.routineService = mainApp.getRoutineService();
    }

    /**
     * Sets the user name and loads the corresponding user data.
     *
     * @param userName Name of the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
        refreshView();
    }

    /**
     * Refreshes the displayed daily and weekly tasks.
     */
    private void refreshView() {
        setPageText();
        clearStatus();

        try {
            Day today = routineService.getToday(userName);
            TaskList dailyRoutines = routineService.getRoutines(userName, RoutineType.DAILY);
            TaskList weeklyRoutines = routineService.getRoutines(userName, RoutineType.WEEKLY);

            loadDailyTasks(today, dailyRoutines);
            loadWeeklyTasks(today, weeklyRoutines);
        } catch (RuntimeException e) {
            handleLoadFailure();
        }
    }

    /**
     * Sets the page title and user label.
     */
    private void setPageText() {
        pageTitleLabel.setText("Today's Tasks");
        userLabel.setText(userName);
    }

    /**
     * Clears the current status message.
     */
    private void clearStatus() {
        statusLabel.setText("");
    }

    /**
     * Loads and displays daily tasks.
     *
     * @param today Today's day record.
     * @param dailyRoutines Daily routine list.
     */
    private void loadDailyTasks(Day today, TaskList dailyRoutines) {
        dailyContainer.getChildren().clear();

        if (dailyRoutines.getAllTasks().isEmpty()) {
            showEmptyLabel(dailyEmptyLabel);
            return;
        }

        hideEmptyLabel(dailyEmptyLabel);

        for (Task task : dailyRoutines.getAllTasks()) {
            CheckBox box = createDailyTaskCheckBox(today, task);
            dailyContainer.getChildren().add(box);
        }
    }

    /**
     * Loads and displays weekly tasks.
     *
     * @param today Today's day record.
     * @param weeklyRoutines Weekly routine list.
     */
    private void loadWeeklyTasks(Day today, TaskList weeklyRoutines) {
        weeklyContainer.getChildren().clear();

        if (weeklyRoutines.getAllTasks().isEmpty()) {
            showEmptyLabel(weeklyEmptyLabel);
            return;
        }

        hideEmptyLabel(weeklyEmptyLabel);

        for (Task task : weeklyRoutines.getAllTasks()) {
            CheckBox box = createWeeklyTaskCheckBox(today, task);
            weeklyContainer.getChildren().add(box);
        }
    }

    /**
     * Creates a check box for a daily task.
     *
     * @param today Today's day record.
     * @param task Task to display.
     * @return Configured check box.
     */
    private CheckBox createDailyTaskCheckBox(Day today, Task task) {
        CheckBox box = new CheckBox(task.getDescription());
        box.setSelected(today.isDailyCompleted(task.getDescription()));
        box.setWrapText(true);

        box.setOnAction(e -> handleDailyTaskToggle(box, task));

        return box;
    }

    /**
     * Creates a check box for a weekly task.
     *
     * @param today Today's day record.
     * @param task Task to display.
     * @return Configured check box.
     */
    private CheckBox createWeeklyTaskCheckBox(Day today, Task task) {
        CheckBox box = new CheckBox(task.getDescription());
        box.setSelected(today.isWeeklyCompleted(task.getDescription()));
        box.setWrapText(true);

        box.setOnAction(e -> handleWeeklyTaskToggle(box, task));

        return box;
    }

    /**
     * Handles toggling of a daily task.
     *
     * @param box Check box that was toggled.
     * @param task Task linked to the check box.
     */
    private void handleDailyTaskToggle(CheckBox box, Task task) {
        boolean previous = !box.isSelected();

        try {
            routineService.setDailyCompleted(userName, task.getDescription(), box.isSelected());
            statusLabel.setText("Saved.");
        } catch (RuntimeException ex) {
            box.setSelected(previous);
            statusLabel.setText("Save failed. Please try again.");
        }
    }

    /**
     * Handles toggling of a weekly task.
     *
     * @param box Check box that was toggled.
     * @param task Task linked to the check box.
     */
    private void handleWeeklyTaskToggle(CheckBox box, Task task) {
        boolean previous = !box.isSelected();

        try {
            routineService.setWeeklyCompleted(userName, task.getDescription(), box.isSelected());
            statusLabel.setText("Saved.");
        } catch (RuntimeException ex) {
            box.setSelected(previous);
            statusLabel.setText("Save failed. Please try again.");
        }
    }

    /**
     * Shows an empty-state label.
     *
     * @param label Label to show.
     */
    private void showEmptyLabel(Label label) {
        label.setVisible(true);
        label.setManaged(true);
    }

    /**
     * Hides an empty-state label.
     *
     * @param label Label to hide.
     */
    private void hideEmptyLabel(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    /**
     * Handles failure to load task data.
     */
    private void handleLoadFailure() {
        dailyContainer.getChildren().clear();
        weeklyContainer.getChildren().clear();

        showEmptyLabel(dailyEmptyLabel);
        showEmptyLabel(weeklyEmptyLabel);

        statusLabel.setText("Unable to load tasks. Please try again.");
    }

    /**
     * Opens the senior log scene.
     */
    @FXML
    private void handleGoToLog() {
        mainApp.showSeniorLogScene(userName);
    }

    /**
     * Returns the user to the login scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showLoginScene();
    }
}
