package futar.futar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    //private OtpServiceManager otpServiceManager;
    @Override
    public void start(Stage primaryStage) throws Exception {
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
    }


}
