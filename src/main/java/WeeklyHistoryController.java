import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import xmoke.Day;
import xmoke.Storage;
import xmoke.Task;
import xmoke.User;

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
    private Storage storage;
    private String userName;

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
        User user = storage.loadUser(userName);
        titleLabel.setText(userName);

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        periodLabel.setText(
                "Time period: " + start.format(formatter) + " to " + end.format(formatter)
        );

        contentContainer.getChildren().clear();

        for (Task task : user.getDailyRoutines().getAllTasks()) {
            int completedCount = 0;
            List<String> missedDays = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate date = start.plusDays(i);
                Day day = user.getDay(date);
                if (day.isDailyCompleted(task.getDescription())) {
                    completedCount++;
                } else {
                    missedDays.add(
                            date.getDayOfWeek().toString().substring(0, 1)
                                    + date.getDayOfWeek().toString().substring(1).toLowerCase()
                    );
                }
            }

            VBox block = new VBox(3);
            block.getChildren().add(new Label(task.getDescription() + ": " + completedCount + "/7"));
            block.getChildren().add(
                    new Label("Missed on: " + (missedDays.isEmpty() ? "-" : String.join(", ", missedDays)))
            );
            contentContainer.getChildren().add(block);
        }

        for (Task task : user.getWeeklyRoutines().getAllTasks()) {
            boolean done = false;
            List<String> doneDays = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate date = start.plusDays(i);
                Day day = user.getDay(date);
                if (day.isWeeklyCompleted(task.getDescription())) {
                    done = true;
                    doneDays.add(
                            date.getDayOfWeek().toString().substring(0, 1)
                                    + date.getDayOfWeek().toString().substring(1).toLowerCase()
                    );
                }
            }

            VBox block = new VBox(3);
            block.getChildren().add(
                    new Label(task.getDescription() + ": " + (done ? "Done this week" : "Not done this week"))
            );
            block.getChildren().add(
                    new Label("Marked on: " + (doneDays.isEmpty() ? "-" : String.join(", ", doneDays)))
            );
            contentContainer.getChildren().add(block);
        }

        storage.saveUser(user);
    }

    /**
     * Returns the user to the history user selection scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showHistorySelectUserScene("week");
    }
}
