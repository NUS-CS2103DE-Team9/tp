package HealthcareEveryday.controller;

import java.util.Optional;

import HealthcareEveryday.MainApp;
import HealthcareEveryday.model.RoutineType;
import HealthcareEveryday.model.Task;
import HealthcareEveryday.model.TaskList;
import HealthcareEveryday.service.RoutineService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

        displayRoutines(dailyContainer, dailyRoutines, RoutineType.DAILY);
        displayRoutines(weeklyContainer, weeklyRoutines, RoutineType.WEEKLY);
    }

    /**
     * Displays all routines of one type in the given container.
     *
     * @param container Container to update.
     * @param routines Routine list to display.
     * @param type Type of routine being displayed.
     */
    private void displayRoutines(VBox container, TaskList routines, RoutineType type) {
        container.getChildren().clear();

        for (Task task : routines.getAllTasks()) {
            HBox row = createRoutineRow(task.getDescription(), type);
            container.getChildren().add(row);
        }
    }

    /**
     * Creates one routine row with label and remove button.
     *
     * @param description Description of the routine.
     * @param type Type of routine.
     * @return Routine row.
     */
    private HBox createRoutineRow(String description, RoutineType type) {
        HBox row = new HBox(10);
        row.getStyleClass().add("edit-row");

        Label label = new Label(description);

        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().addAll("danger", "compact");
        removeButton.setOnAction(e -> removeRoutine(description, type));

        row.getChildren().addAll(label, removeButton);
        return row;
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
        Optional<String> result = showAddTaskDialog();

        if (result.isEmpty()) {
            return;
        }

        String trimmedName = result.get().trim();

        if (trimmedName.isEmpty()) {
            showInvalidTaskName();
            return;
        }

        boolean added = routineService.addRoutine(userName, trimmedName, type);

        if (!added) {
            showDuplicateTask(type);
            return;
        }

        refreshView();
    }

    /**
     * Shows the add-task dialog.
     *
     * @return Entered task name, if provided.
     */
    private Optional<String> showAddTaskDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Task");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter task name:");
        return dialog.showAndWait();
    }

    /**
     * Shows an invalid task-name message.
     */
    private void showInvalidTaskName() {
        showInfo("Invalid task", "Task name cannot be empty.");
    }

    /**
     * Shows a duplicate-task message.
     *
     * @param type Routine type where duplication happened.
     */
    private void showDuplicateTask(RoutineType type) {
        String listName = type.name().toLowerCase();
        String message = "This task already exists in the " + listName + " list.";
        showInfo("Duplicate task", message);
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
