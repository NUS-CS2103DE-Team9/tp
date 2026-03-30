import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;

public class LoginController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        loadUsers();
    }

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

    @FXML
    private void handleCaregiver() {
        mainApp.showCaregiverLoginScene();
    }
}
