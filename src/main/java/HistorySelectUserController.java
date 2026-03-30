import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Controller for the history user selection view.
 */
public class HistorySelectUserController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private String period;

    /**
     * Sets the main application reference for scene switching.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Sets the selected history period and loads the users.
     *
     * @param period Selected period (e.g. "week").
     */
    public void setPeriod(String period) {
        this.period = period;
        loadUsers();
    }

    /**
     * Loads the list of users and displays them as buttons.
     */
    private void loadUsers() {
        userContainer.getChildren().clear();
        List<String> users = mainApp.getStorage().listSeniorNames();

        for (String user : users) {
            Button button = new Button(user);
            button.setPrefWidth(220);
            button.setOnAction(e -> {
                if ("week".equals(period)) {
                    mainApp.showWeeklyHistoryScene(user);
                }
            });
            userContainer.getChildren().add(button);
        }
    }

    /**
     * Returns the user to the history period selection scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showHistoryPeriodScene();
    }
}
