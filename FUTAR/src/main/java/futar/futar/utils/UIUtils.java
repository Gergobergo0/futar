package futar.futar.utils;

import javafx.scene.control.Alert;

public class UIUtils {

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Információ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
