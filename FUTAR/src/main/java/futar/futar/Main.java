package futar.futar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
//https://repo.itk.ppke.hu/java/gelge1
public class Main extends Application {
    //private OtpServiceManager otpServiceManager;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/main_view.fxml"));
        Scene scene = new Scene(loader.load());
        try {
            URL iconURL = getClass().getResource("/images/FUTAR_ICON.png");
            if (iconURL != null) {
                java.awt.Image awtImage = Toolkit.getDefaultToolkit().getImage(iconURL);
                com.apple.eawt.Application.getApplication().setDockIconImage(awtImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream iconStream = getClass().getResourceAsStream("/images/FUTAR_ICON.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        }



        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/FUTAR_ICON.png")));

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
        System.out.println("Alkalmazás leáll... OTP szerver leállítása");
    }


}
