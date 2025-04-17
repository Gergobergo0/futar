package futar.futar.controller.map;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class MapInitializer {
    private final WebEngine webEngine;
    private Object javaConnector;

    public MapInitializer(WebView mapView) {
        this.webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/html/map.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                if (javaConnector != null) {
                    window.setMember("java", javaConnector);
                }
            }
        });
    }

    public void setJavaConnector(Object connector) {
        this.javaConnector = connector;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }
}