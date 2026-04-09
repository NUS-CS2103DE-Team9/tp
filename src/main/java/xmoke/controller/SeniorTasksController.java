package xmoke.controller;

import xmoke.MainApp;
import xmoke.model.Day;
import xmoke.model.RoutineType;
import xmoke.model.Task;
import xmoke.model.TaskList;
import xmoke.service.RoutineService;

import java.util.List;

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
        pageTitleLabel.setText("Today's Tasks");
        userLabel.setText(userName);
        statusLabel.setText("");

        Day today = routineService.getToday(userName);
        TaskList dailyRoutines = routineService.getRoutines(userName, RoutineType.DAILY);
        TaskList weeklyRoutines = routineService.getRoutines(userName, RoutineType.WEEKLY);

        dailyContainer.getChildren().clear();
        if (dailyRoutines.getAllTasks().isEmpty()) {
            dailyEmptyLabel.setVisible(true);
            dailyEmptyLabel.setManaged(true);
        } else {
            dailyEmptyLabel.setVisible(false);
            dailyEmptyLabel.setManaged(false);

            for (Task task : dailyRoutines.getAllTasks()) {
                CheckBox box = new CheckBox(task.getDescription());
                box.setSelected(today.isDailyCompleted(task.getDescription()));
                box.setWrapText(true);
                box.setOnAction(e -> {
                    routineService.setDailyCompleted(userName, task.getDescription(), box.isSelected());
                    statusLabel.setText("Saved.");
                });
                dailyContainer.getChildren().add(box);
            }
        }

        weeklyContainer.getChildren().clear();
        if (weeklyRoutines.getAllTasks().isEmpty()) {
            weeklyEmptyLabel.setVisible(true);
            weeklyEmptyLabel.setManaged(true);
        } else {
            weeklyEmptyLabel.setVisible(false);
            weeklyEmptyLabel.setManaged(false);

            for (Task task : weeklyRoutines.getAllTasks()) {
                CheckBox box = new CheckBox(task.getDescription());
                box.setSelected(today.isWeeklyCompleted(task.getDescription()));
                box.setWrapText(true);
                box.setOnAction(e -> {
                    routineService.setWeeklyCompleted(userName, task.getDescription(), box.isSelected());
                    statusLabel.setText("Saved.");
                });
                weeklyContainer.getChildren().add(box);
            }
        }
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
