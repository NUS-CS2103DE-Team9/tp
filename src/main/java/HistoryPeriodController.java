import javafx.fxml.FXML;

public class HistoryPeriodController {
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleToday() {
        mainApp.showTodayHistoryScene();
    }

    @FXML
    private void handlePastWeek() {
        mainApp.showHistorySelectUserScene("week");
    }

    @FXML
    private void handleBack() {
        mainApp.showCaregiverMenuScene();
    }
}
