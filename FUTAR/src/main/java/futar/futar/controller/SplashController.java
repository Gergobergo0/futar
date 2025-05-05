package futar.futar.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class SplashController {
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator progressIndicator;

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public void setProgress(double progress) {
        progressIndicator.setProgress(progress);
    }
}
