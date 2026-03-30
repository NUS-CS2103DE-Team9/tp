import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Controller for the login view.
 */
public class LoginController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;

    /**
     * Sets the main application reference and loads the users.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
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
            button.setOnAction(e -> mainApp.showSeniorTasksScene(user));
            userContainer.getChildren().add(button);
        }
    }

    /**
     * Opens the caregiver login scene.
     */
    @FXML
    private void handleCaregiver() {
        mainApp.showCaregiverLoginScene();
    }
}
