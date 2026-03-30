import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xmoke.Day;
import xmoke.Storage;
import xmoke.Task;
import xmoke.User;

import java.time.LocalDate;
import java.util.List;

public class TodayHistoryController {
    @FXML
    private VBox usersContainer;

    private MainApp mainApp;
    private Storage storage;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.storage = mainApp.getStorage();
        loadTodayHistory();
    }

    private void loadTodayHistory() {
        usersContainer.getChildren().clear();
        List<String> users = storage.listSeniorNames();

        for (String userName : users) {
            User user = storage.loadUser(userName);
            Day today = user.getDay(LocalDate.now());

            VBox userBox = new VBox(8);
            Label nameLabel = new Label(userName);
            nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

            userBox.getChildren().add(nameLabel);
            userBox.getChildren().add(new Label("Daily"));
            for (Task task : user.getDailyRoutines().getAllTasks()) {
                String mark = today.isDailyCompleted(task.getDescription()) ? "[✓] " : "[ ] ";
                userBox.getChildren().add(new Label(mark + task.getDescription()));
            }

            userBox.getChildren().add(new Label("Weekly"));
            for (Task task : user.getWeeklyRoutines().getAllTasks()) {
                String mark = today.isWeeklyCompleted(task.getDescription()) ? "[✓] " : "[ ] ";
                userBox.getChildren().add(new Label(mark + task.getDescription()));
            }

            HBox row = new HBox(40);
            row.getChildren().add(userBox);
            usersContainer.getChildren().add(row);
        }
    }

    @FXML
    private void handleBack() {
        mainApp.showHistoryPeriodScene();
    }
}
