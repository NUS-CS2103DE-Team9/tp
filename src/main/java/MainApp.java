import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import xmoke.Storage;

public class MainApp extends Application {
    private Stage primaryStage;
    private final Storage storage = new Storage();

    public static void main(String[] args) {
        launch(args);
    }

    public Storage getStorage() {
        return storage;
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Healthcare Everyday");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        showLoginScene();
        primaryStage.show();
    }

    public void showLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            AnchorPane pane = loader.load();

            LoginController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load login page", e.getMessage());
        }
    }

    public void showSeniorTasksScene(String userName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SeniorTasksView.fxml"));
            AnchorPane pane = loader.load();

            SeniorTasksController controller = loader.getController();
            controller.setMainApp(this);
            controller.setUserName(userName);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load senior tasks page", e.getMessage());
        }
    }

    public void showSeniorLogScene(String userName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SeniorLogView.fxml"));
            AnchorPane pane = loader.load();

            SeniorLogController controller = loader.getController();
            controller.setMainApp(this);
            controller.setUserName(userName);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load senior log page", e.getMessage());
        }
    }

    public void showCaregiverLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CaregiverLoginView.fxml"));
            AnchorPane pane = loader.load();

            CaregiverLoginController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load caregiver login page", e.getMessage());
        }
    }

    public void showCaregiverMenuScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CaregiverMenuView.fxml"));
            AnchorPane pane = loader.load();

            CaregiverMenuController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load caregiver menu", e.getMessage());
        }
    }

    public void showCaregiverSelectUserScene(String mode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CaregiverSelectUserView.fxml"));
            AnchorPane pane = loader.load();

            CaregiverSelectUserController controller = loader.getController();
            controller.setMainApp(this);
            controller.setMode(mode);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load user selection page", e.getMessage());
        }
    }

    public void showEditRoutineScene(String userName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditRoutineView.fxml"));
            AnchorPane pane = loader.load();

            EditRoutineController controller = loader.getController();
            controller.setMainApp(this);
            controller.setUserName(userName);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load edit routine page", e.getMessage());
        }
    }

    public void showHistoryPeriodScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HistoryPeriodView.fxml"));
            AnchorPane pane = loader.load();

            HistoryPeriodController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load history period page", e.getMessage());
        }
    }

    public void showHistorySelectUserScene(String period) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HistorySelectUserView.fxml"));
            AnchorPane pane = loader.load();

            HistorySelectUserController controller = loader.getController();
            controller.setMainApp(this);
            controller.setPeriod(period);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load history user selection page", e.getMessage());
        }
    }

    public void showTodayHistoryScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TodayHistoryView.fxml"));
            AnchorPane pane = loader.load();

            TodayHistoryController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(pane, 900, 650));
        } catch (Exception e) {
            showErrorAlert("Failed to load today's history page", e.getMessage());
        }
    }

    public void showWeeklyHistoryScene(String userName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WeeklyHistoryView.fxml"));
            AnchorPane pane = loader.load();

            WeeklyHistoryController controller = loader.getController();
            controller.setMainApp(this);
            controller.setUserName(userName);

            primaryStage.setScene(new Scene(pane, 900, 650));
        } catch (Exception e) {
            showErrorAlert("Failed to load weekly history page", e.getMessage());
        }
    }

    public void showGenerateSummarySelectUserScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GenerateSummarySelectUserView.fxml"));
            AnchorPane pane = loader.load();

            GenerateSummarySelectUserController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(new Scene(pane, 800, 600));
        } catch (Exception e) {
            showErrorAlert("Failed to load summary generation page", e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message == null ? "Unknown error." : message);
        alert.showAndWait();
    }
}