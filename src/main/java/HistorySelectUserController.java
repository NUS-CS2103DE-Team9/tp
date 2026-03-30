import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;

public class HistorySelectUserController {
    @FXML
    private VBox userContainer;

    private MainApp mainApp;
    private String period;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setPeriod(String period) {
        this.period = period;
        loadUsers();
    }

    private void loadUsers() {
        userContainer.getChildren().clear();
        List<String> users = mainApp.getStorage().listSeniorNames();

        for (String user : users) {
            Button button = new Button(user);
            button.setPrefWidth(220);
            button.setOnAction(e -> {
                if ("week".equals(period)) {
                    mainApp.showWeeklyHistoryScene(user);
                }
            });
            userContainer.getChildren().add(button);
        }
    }

    @FXML
    private void handleBack() {
        mainApp.showHistoryPeriodScene();
    }
}