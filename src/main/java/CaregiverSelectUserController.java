import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class CaregiverSelectUserController {
    @FXML
    private Label titleLabel;

    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private String mode;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setMode(String mode) {
        this.mode = mode;
        if ("edit".equals(mode)) {
            titleLabel.setText("Whose tasks are we modifying?");
        } else {
            titleLabel.setText("Please choose the user to check");
        }
        loadUsers();
    }

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

    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }
}