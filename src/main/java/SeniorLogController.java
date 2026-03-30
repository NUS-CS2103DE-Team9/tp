import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import xmoke.Day;
import xmoke.Storage;
import xmoke.User;

import java.time.LocalDate;

public class SeniorLogController {
    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label userLabel;

    @FXML
    private TextArea logArea;

    @FXML
    private Label statusLabel;

    private MainApp mainApp;
    private Storage storage;
    private String userName;
    private User user;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.storage = mainApp.getStorage();
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.user = storage.loadUser(userName);

        Day today = user.getOrCreateDay(LocalDate.now());
        pageTitleLabel.setText("Daily Log");
        userLabel.setText(userName);
        logArea.setText(today.getLog());
        statusLabel.setText("");
        storage.saveUser(user);
    }

    @FXML
    private void handleSubmit() {
        Day today = user.getOrCreateDay(LocalDate.now());
        today.setLog(logArea.getText());
        storage.saveUser(user);
        statusLabel.setText("Today's record saved.");
    }

    @FXML
    private void handleBack() {
        mainApp.showSeniorTasksScene(userName);
    }
}
