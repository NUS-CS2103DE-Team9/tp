import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the caregiver user selection view.
 */
public class CaregiverSelectUserController {
    @FXML
    private Label titleLabel;

    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private String mode;

    /**
     * Sets the main application reference for scene switching and storage access.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Sets the mode of the controller and updates the UI accordingly.
     *
     * @param mode Mode of operation (e.g. "edit").
     */
    public void setMode(String mode) {
        this.mode = mode;
        if ("edit".equals(mode)) {
            titleLabel.setText("Whose tasks are we modifying?");
        } else {
            titleLabel.setText("Please choose the user to check");
        }
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
                if ("edit".equals(mode)) {
                    mainApp.showEditRoutineScene(user);
                }
            });
            userContainer.getChildren().add(button);
        }
    }

    /**
     * Returns the user to the caregiver menu scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }
}
