package xmoke.controller;

import xmoke.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xmoke.model.RoutineType;
import xmoke.model.Task;
import xmoke.model.TaskList;
import xmoke.service.RoutineService;

/**
 * Controller for the edit routine view.
 */
public class EditRoutineController {
    @FXML
    private Label titleLabel;

    @FXML
    private VBox dailyContainer;

    @FXML
    private VBox weeklyContainer;

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
     * @param userName Name of the selected user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
        refreshView();
    }

    /**
     * Refreshes the displayed daily and weekly routines.
     */
    private void refreshView() {
        titleLabel.setText("Tasks for " + userName);

        TaskList dailyRoutines = routineService.getRoutines(userName, RoutineType.DAILY);
        TaskList weeklyRoutines = routineService.getRoutines(userName, RoutineType.WEEKLY);

        dailyContainer.getChildren().clear();
        for (Task task : dailyRoutines.getAllTasks()) {
            HBox row = new HBox(10);
            row.getStyleClass().add("edit-row");
            Label label = new Label(task.getDescription());
            Button removeButton = new Button("Remove");
            removeButton.getStyleClass().addAll("danger", "compact");
            removeButton.setOnAction(e -> removeRoutine(task.getDescription(), RoutineType.DAILY));
            row.getChildren().addAll(label, removeButton);
            dailyContainer.getChildren().add(row);
        }

        weeklyContainer.getChildren().clear();
        for (Task task : weeklyRoutines.getAllTasks()) {
            HBox row = new HBox(10);
            row.getStyleClass().add("edit-row");
            Label label = new Label(task.getDescription());
            Button removeButton = new Button("Remove");
            removeButton.getStyleClass().addAll("danger", "compact");
            removeButton.setOnAction(e -> removeRoutine(task.getDescription(), RoutineType.WEEKLY));
            row.getChildren().addAll(label, removeButton);
            weeklyContainer.getChildren().add(row);
        }
    }

    /**
     * Removes a routine with the given description from the specified routine type.
     *
     * @param description Description of the routine to remove.
     * @param type Type of routine to remove from.
     */
    private void removeRoutine(String description, RoutineType type) {
        routineService.removeRoutine(userName, description, type);
        refreshView();
    }

    /**
     * Handles adding a new daily task.
     */
    @FXML
    private void handleAddDailyTask() {
        addRoutine(RoutineType.DAILY);
    }

    /**
     * Handles adding a new weekly task.
     */
    @FXML
    private void handleAddWeeklyTask() {
        addRoutine(RoutineType.WEEKLY);
    }

    /**
     * Adds a new routine of the specified type.
     *
     * @param type Type of routine to add.
     */
    private void addRoutine(RoutineType type) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Task");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter task name:");

        dialog.showAndWait().ifPresent(name -> {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) {
                showInfo("Invalid task", "Task name cannot be empty.");
                return;
            }

            boolean added = routineService.addRoutine(userName, trimmed, type);
            if (!added) {
                showInfo(
                        "Duplicate task",
                        "This task already exists in the " + type.name().toLowerCase() + " list."
                );
                return;
            }

            refreshView();
        });
    }

    /**
     * Returns the user to the caregiver menu scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }

    /**
     * Shows an information alert with the given title and message.
     *
     * @param title Title of the alert.
     * @param message Message to display.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
