package futar.futar.utils;

import futar.futar.api.ApiClientProvider;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Alert.AlertType;

public class UIUtils {

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Információ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static void showConnectionDialog() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kapcsolódás");
            alert.setHeaderText("Csatlakozás a BKK szerverhez...");
            alert.setContentText("Kérlek, várj. Az ablak automatikusan bezárul, ha a kapcsolat helyreáll.");
            alert.getButtonTypes().clear(); // Nincsenek gombok alapból

            alert.setOnCloseRequest(event -> event.consume()); // Ne lehessen bezárni X-szel
            alert.show();

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.setOnCloseRequest(event -> event.consume());

            // Hálózatfigyelés külön szálon
            new Thread(() -> {
                int maxRetriesBeforeError = 3;
                int retries = 0;
                boolean errorShown = false;

                while (true) {
                    boolean available = NetworkUtils.isApiReachable();

                    if (available) {
                        Platform.runLater(stage::close);
                        break;
                    }

                    retries++;

                    if (!errorShown && retries >= maxRetriesBeforeError) {
                        errorShown = true;
                        Platform.runLater(() -> {
                            alert.setAlertType(Alert.AlertType.ERROR);
                            alert.setTitle("Nincs internetkapcsolat");
                            alert.setHeaderText("Nem sikerült csatlakozni a BKK szerverhez");
                            alert.setContentText("Ellenőrizd az internetkapcsolatot.\nAz ablak automatikusan bezárul, ha sikerül csatlakozni.");

                            // ➕ Kilépés gomb hozzáadása
                            ButtonType exitButton = new ButtonType("Kilépés", ButtonBar.ButtonData.CANCEL_CLOSE);
                            alert.getButtonTypes().setAll(exitButton);

                            alert.resultProperty().addListener((obs, oldVal, newVal) -> {
                                if (newVal == exitButton) {
                                    Platform.exit();
                                    System.exit(0);
                                }
                            });
                        });
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {}
                }
            }).start();
        });
    }

    public static String escapeJs(String input) {
        return input == null ? "" : input
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "");
    }




}
