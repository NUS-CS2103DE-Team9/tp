import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import xmoke.Day;
import xmoke.Storage;
import xmoke.Task;
import xmoke.User;

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
    private Storage storage;
    private String userName;
    private User user;

    /**
     * Sets the main application reference and storage reference.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.storage = mainApp.getStorage();
    }

    /**
     * Sets the user name and loads the corresponding user data.
     *
     * @param userName Name of the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
        this.user = storage.loadUser(userName);
        refreshView();
    }

    /**
     * Refreshes the displayed daily and weekly tasks.
     */
    private void refreshView() {
        pageTitleLabel.setText("Today's Tasks");
        userLabel.setText(userName);
        statusLabel.setText("");

        Day today = user.getOrCreateDay(LocalDate.now());

        dailyContainer.getChildren().clear();
        if (user.getDailyRoutines().getAllTasks().isEmpty()) {
            dailyEmptyLabel.setVisible(true);
            dailyEmptyLabel.setManaged(true);
        } else {
            dailyEmptyLabel.setVisible(false);
            dailyEmptyLabel.setManaged(false);

            for (Task task : user.getDailyRoutines().getAllTasks()) {
                CheckBox box = new CheckBox(task.getDescription());
                box.setSelected(today.isDailyCompleted(task.getDescription()));
                box.setWrapText(true);
                box.setOnAction(e -> {
                    today.setDailyCompleted(task.getDescription(), box.isSelected());
                    storage.saveUser(user);
                    statusLabel.setText("Saved.");
                });
                dailyContainer.getChildren().add(box);
            }
        }

        weeklyContainer.getChildren().clear();
        if (user.getWeeklyRoutines().getAllTasks().isEmpty()) {
            weeklyEmptyLabel.setVisible(true);
            weeklyEmptyLabel.setManaged(true);
        } else {
            weeklyEmptyLabel.setVisible(false);
            weeklyEmptyLabel.setManaged(false);

            for (Task task : user.getWeeklyRoutines().getAllTasks()) {
                CheckBox box = new CheckBox(task.getDescription());
                box.setSelected(today.isWeeklyCompleted(task.getDescription()));
                box.setWrapText(true);
                box.setOnAction(e -> {
                    today.setWeeklyCompleted(task.getDescription(), box.isSelected());
                    storage.saveUser(user);
                    statusLabel.setText("Saved.");
                });
                weeklyContainer.getChildren().add(box);
            }
        }

        storage.saveUser(user);
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
