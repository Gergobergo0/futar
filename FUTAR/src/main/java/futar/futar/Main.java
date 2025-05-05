package futar.futar;

import futar.futar.controller.SplashController;
import futar.futar.persistence.GtfsDownloader;
import futar.futar.service.OtpServiceManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;

public class Main extends Application {
    private void startOtp()
    {

    }


    //private OtpServiceManager otpServiceManager;
    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
        FXMLLoader splashLoader = new FXMLLoader(Main.class.getResource("/fxml/splash.fxml"));
        Scene splashScene = new Scene(splashLoader.load());
        SplashController splashController = splashLoader.getController();


        Stage splashStage = new Stage();
        splashStage.setScene(splashScene);
        splashStage.setTitle("FUTÁR – Indítás");
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.show();

        otpServiceManager = new OtpServiceManager();

        new Thread(() -> {
            otpServiceManager.startOtpServerBlocking(progress -> {
                Platform.runLater(() -> {
                    if (progress == 0.3) splashController.updateStatus("Graph inicializálása...");
                    if (progress == 0.6) splashController.updateStatus("Úthálózat betöltése...");
                    if (progress == 1.0) splashController.updateStatus("Szerver elindult ✅");
                    splashController.setProgress(progress);
                });
            });

            Platform.runLater(() -> {
                try {
                    splashStage.close();

                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/main_view.fxml"));
                    Scene mainScene = new Scene(loader.load());
                    Stage mainStage = new Stage();
                    mainStage.setTitle("FUTÁR");
                    mainStage.setScene(mainScene);
                    mainStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();*/
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/main_view.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("FUTÁR");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        System.out.println("Starting...");

        launch();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Alkalmazás leáll... OTP szerver leállítása.");
        //otpServiceManager.stopOtpServer();
    }


}
