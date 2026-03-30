import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class CaregiverMenuController {
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleEditRoutine() {
        mainApp.showCaregiverSelectUserScene("edit");
    }

    @FXML
    private void handleViewHistory() {
        mainApp.showHistoryPeriodScene();
    }

    @FXML
    private void handleGenerateSummary() {
        mainApp.showGenerateSummarySelectUserScene();
    }

    @FXML
    private void handleAddUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New User");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the name of the new user:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String name = result.get().trim();
        if (name.isEmpty()) {
            showInfo("Invalid name", "User name cannot be empty.");
            return;
        }

        boolean added = mainApp.getStorage().addUser(name);
        if (added) {
            showInfo("Success", "User \"" + name + "\" has been added.");
        } else {
            showInfo("Failed", "A user with that name already exists, or the name is invalid.");
        }
    }

    @FXML
    private void handleChangePassword() {
        TextInputDialog oldDialog = new TextInputDialog();
        oldDialog.setTitle("Change Password");
        oldDialog.setHeaderText(null);
        oldDialog.setContentText("Enter old password:");
        Optional<String> oldResult = oldDialog.showAndWait();
        if (oldResult.isEmpty()) {
            return;
        }

        TextInputDialog newDialog = new TextInputDialog();
        newDialog.setTitle("Change Password");
        newDialog.setHeaderText(null);
        newDialog.setContentText("Enter new password:");
        Optional<String> newResult = newDialog.showAndWait();
        if (newResult.isEmpty()) {
            return;
        }

        String oldPassword = oldResult.get();
        String newPassword = newResult.get();

        boolean changed = mainApp.getStorage().changeCaregiverPassword(oldPassword, newPassword);
        if (changed) {
            showInfo("Success", "Password updated successfully.");
        } else {
            showInfo("Failed", "Old password is incorrect or new password is empty.");
        }
    }

    @FXML
    private void handleBack() {
        mainApp.showLoginScene();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}