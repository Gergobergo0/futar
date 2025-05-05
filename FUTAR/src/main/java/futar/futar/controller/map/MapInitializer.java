package futar.futar.controller.map;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class MapInitializer {
    private final WebEngine webEngine;
    private Object javaConnector;
    private boolean htmlLoaded = false;


    public MapInitializer(WebView mapView) {
        this.webEngine = mapView.getEngine();
        //webEngine.load(getClass().getResource("/html/map.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("✅ HTML betöltve, próbálom injektálni a java bridge-et");
                injectJavaConnector();
            }
        });

    }
    public void executeScript(String script) {
        Platform.runLater(() -> webEngine.executeScript(script));
    }


    public void setJavaConnector(Object connector) {
        this.javaConnector = connector;

        if (webEngine.getLoadWorker().getState() == javafx.concurrent.Worker.State.SUCCEEDED) {
            System.out.println("✅ Java bridge utólag beállítva");
            injectJavaConnector();
        }
    }
    private void injectJavaConnector() {
        if (javaConnector == null) {
            System.out.println("⚠️ JavaConnector még null, nem lehet injektálni.");
            return;
        }

        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("java", javaConnector);
        System.out.println("✅ JavaConnector injektálva JavaScript-be");
    }
    public void startLoad() {
        webEngine.load(getClass().getResource("/html/map.html").toExternalForm());
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }
}