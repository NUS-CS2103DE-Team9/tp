import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import xmoke.Storage;

/**
 * Main JavaFX application for the Healthcare Everyday app.
 */
public class MainApp extends Application {
    private Stage primaryStage;
    private final Storage storage = new Storage();

    /**
     * Launches the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns the shared storage instance used by the application.
     *
     * @return Storage instance.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Starts the JavaFX application and shows the login scene.
     *
     * @param stage Primary stage of the application.
     */
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Healthcare Everyday");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        showLoginScene();
        primaryStage.show();
    }

    /**
     * Loads and shows the login scene.
     */
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

    /**
     * Loads and shows the senior tasks scene for the specified user.
     *
     * @param userName Name of the user.
     */
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

    /**
     * Loads and shows the senior log scene for the specified user.
     *
     * @param userName Name of the user.
     */
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

    /**
     * Loads and shows the caregiver login scene.
     */
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

    /**
     * Loads and shows the caregiver menu scene.
     */
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

    /**
     * Loads and shows the caregiver user selection scene.
     *
     * @param mode Mode for the selection scene.
     */
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

    /**
     * Loads and shows the edit routine scene for the specified user.
     *
     * @param userName Name of the user.
     */
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

    /**
     * Loads and shows the history period selection scene.
     */
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

    /**
     * Loads and shows the history user selection scene.
     *
     * @param period Selected history period.
     */
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

    /**
     * Loads and shows the today's history scene.
     */
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

    /**
     * Loads and shows the weekly history scene for the specified user.
     *
     * @param userName Name of the user.
     */
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

    /**
     * Loads and shows the summary generation user selection scene.
     */
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

    /**
     * Shows an error alert with the given title and message.
     *
     * @param title Title of the alert.
     * @param message Message to display.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message == null ? "Unknown error." : message);
        alert.showAndWait();
    }
}
