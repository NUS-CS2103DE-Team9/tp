import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Controller for the home page: user selection and Enter to open chat.
 */
public class HomePageController {

    private static final String[] USER_NAMES = {
        "Obi-Wan Kenobi",
        "Mace Windu",
        "Qui-Gon Jinn",
        "Luke Skywalker"
    };

    @FXML
    private ComboBox<String> userCombo;

    private MainApp mainApp;

    /** Called by FXML loader. */
    @FXML
    public void initialize() {
        userCombo.getItems().setAll(USER_NAMES);
        userCombo.getSelectionModel().selectFirst();
    }

    /** Sets the main app so we can switch to the chat scene. */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /** Opens the chat window for the selected user. */
    @FXML
    private void handleEnter() {
        String selected = userCombo.getSelectionModel().getSelectedItem();
        if (selected == null || selected.isBlank()) {
            return;
        }
        xmoke.Storage storage = new xmoke.Storage(selected);
        xmoke.Xmoke xmoke = new xmoke.Xmoke(storage);
        mainApp.showChatScene(selected, xmoke);
    }
}
