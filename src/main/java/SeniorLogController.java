import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import xmoke.Day;
import xmoke.Storage;
import xmoke.User;

/**
 * Controller for the senior daily log view.
 */
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

    /**
     * Sets the main application reference and storage reference.
     *
     * @param mainApp Main application instance.
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.storage = mainApp.getStorage();
    }

    /**
     * Sets the user name and loads today's log data.
     *
     * @param userName Name of the user.
     */
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

    /**
     * Handles submitting and saving today's log.
     */
    @FXML
    private void handleSubmit() {
        Day today = user.getOrCreateDay(LocalDate.now());
        today.setLog(logArea.getText());
        storage.saveUser(user);
        statusLabel.setText("Today's record saved.");
    }

    /**
     * Returns the user to the senior tasks scene.
     */
    @FXML
    private void handleBack() {
        mainApp.showSeniorTasksScene(userName);
    }
}
