import java.nio.file.Path;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import xmoke.Storage;
import xmoke.SummaryGenerator;
import xmoke.User;

/**
 * Controller for the summary generation user selection view.
 */
public class GenerateSummarySelectUserController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private Storage storage;

    /**
     * Sets the main application reference and storage reference.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.storage = mainApp.getStorage();
        loadUsers();
    }

    /**
     * Loads the list of users and displays them as buttons.
     */
    private void loadUsers() {
        userContainer.getChildren().clear();
        List<String> users = storage.listSeniorNames();

        for (String userName : users) {
            Button button = new Button(userName);
            button.setPrefWidth(240);
            button.setOnAction(e -> generateSummary(userName));
            userContainer.getChildren().add(button);
        }
    }

    /**
     * Generates a monthly summary report for the specified user.
     *
     * @param userName Name of the user.
     */
    private void generateSummary(String userName) {
        try {
            User user = storage.loadUser(userName);
            SummaryGenerator generator = new SummaryGenerator();
            Path reportPath = generator.generateMonthlySummary(user);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Generated");
            alert.setHeaderText(null);
            alert.setContentText("Report has been generated for:\n"
                    + userName
                    + "\n\nPlease check:\n"
                    + reportPath.toString());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Generation Failed");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
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
