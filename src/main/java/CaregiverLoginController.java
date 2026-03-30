import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class CaregiverLoginController {
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleLogin() {
        boolean valid = mainApp.getStorage().validateCaregiverPassword(passwordField.getText());
        if (valid) {
            errorLabel.setText("");
            mainApp.showCaregiverMenuScene();
        } else {
            errorLabel.setText("Wrong password, please try again.");
        }
    }

    @FXML
    private void handleBack() {
        mainApp.showLoginScene();
    }
}
