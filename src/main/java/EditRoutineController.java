import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import xmoke.RoutineType;
import xmoke.Storage;
import xmoke.Task;
import xmoke.User;

public class EditRoutineController {
    @FXML
    private Label titleLabel;

    @FXML
    private VBox dailyContainer;

    @FXML
    private VBox weeklyContainer;

    private MainApp mainApp;
    private Storage storage;
    private String userName;
    private User user;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.storage = mainApp.getStorage();
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.user = storage.loadUser(userName);
        refreshView();
    }

    private void refreshView() {
        titleLabel.setText("Tasks for " + userName);

        dailyContainer.getChildren().clear();
        for (Task task : user.getDailyRoutines().getAllTasks()) {
            HBox row = new HBox(10);
            Label label = new Label(task.getDescription());
            Button removeButton = new Button("Remove");
            removeButton.setOnAction(e -> {
                removeRoutine(task.getDescription(), RoutineType.DAILY);
            });
            row.getChildren().addAll(label, removeButton);
            dailyContainer.getChildren().add(row);
        }

        weeklyContainer.getChildren().clear();
        for (Task task : user.getWeeklyRoutines().getAllTasks()) {
            HBox row = new HBox(10);
            Label label = new Label(task.getDescription());
            Button removeButton = new Button("Remove");
            removeButton.setOnAction(e -> {
                removeRoutine(task.getDescription(), RoutineType.WEEKLY);
            });
            row.getChildren().addAll(label, removeButton);
            weeklyContainer.getChildren().add(row);
        }
    }

    private void removeRoutine(String description, RoutineType type) {
        if (type == RoutineType.DAILY) {
            for (int i = 0; i < user.getDailyRoutines().size(); i++) {
                if (user.getDailyRoutines().getTask(i).getDescription().equals(description)) {
                    user.getDailyRoutines().removeTask(i);
                    break;
                }
            }
        } else {
            for (int i = 0; i < user.getWeeklyRoutines().size(); i++) {
                if (user.getWeeklyRoutines().getTask(i).getDescription().equals(description)) {
                    user.getWeeklyRoutines().removeTask(i);
                    break;
                }
            }
        }
        storage.saveUser(user);
        refreshView();
    }

    @FXML
    private void handleAddDailyTask() {
        addRoutine(RoutineType.DAILY);
    }

    @FXML
    private void handleAddWeeklyTask() {
        addRoutine(RoutineType.WEEKLY);
    }

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

            if (routineExists(trimmed, type)) {
                showInfo("Duplicate task", "This task already exists in the " + type.name().toLowerCase() + " list.");
                return;
            }

            if (type == RoutineType.DAILY) {
                user.getDailyRoutines().addTask(new Task(trimmed, RoutineType.DAILY));
            } else {
                user.getWeeklyRoutines().addTask(new Task(trimmed, RoutineType.WEEKLY));
            }

            storage.saveUser(user);
            refreshView();
        });
    }

    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }

    private boolean routineExists(String name, RoutineType type) {
        String trimmed = name.trim();

        if (type == RoutineType.DAILY) {
            for (Task task : user.getDailyRoutines().getAllTasks()) {
                if (task.getDescription().equalsIgnoreCase(trimmed)) {
                    return true;
                }
            }
        } else {
            for (Task task : user.getWeeklyRoutines().getAllTasks()) {
                if (task.getDescription().equalsIgnoreCase(trimmed)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}