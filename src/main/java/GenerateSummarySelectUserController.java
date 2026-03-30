import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import xmoke.Storage;
import xmoke.SummaryGenerator;
import xmoke.User;

import java.nio.file.Path;
import java.util.List;

public class GenerateSummarySelectUserController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private Storage storage;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.storage = mainApp.getStorage();
        loadUsers();
    }

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

    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }
}
