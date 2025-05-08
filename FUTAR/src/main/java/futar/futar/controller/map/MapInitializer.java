package futar.futar.controller.map;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
/**
 * Felelős a térképes WebView inicializálásáért és a Java <-> JavaScript híd (bridge) beállításáért.
 * <p>
 * Betölti a HTML alapú térképet, és biztosítja a Java-oldali objektum elérhetőségét JavaScriptből.
 */
public class MapInitializer {
    /**
     * A WebEngine, amely a HTML rendereléséért és JavaScript végrehajtásáért felel.
     */
    private final WebEngine webEngine;
    /**
     * A Java-oldali objektum, amelyet a JavaScript számára elérhetővé teszünk.
     */
    private Object javaConnector;

    /**
     * Létrehozza a {@code MapInitializer} példányt, és beállítja a HTML betöltését követő híd-injektálást.
     *
     * @param mapView a WebView komponens, amely a térképet jeleníti meg
     */
    public MapInitializer(WebView mapView) {
        this.webEngine = mapView.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("[DEBUG] HTML, Bridge betöltve");
                injectJavaConnector();
            }
        });

    }

    /**
     * JavaScript kódot hajt végre a WebView környezetében.
     * A művelet a JavaFX UI szálon történik.
     *
     * @param script a futtatandó JavaScript kód
     */

    public void executeScript(String script) {
        Platform.runLater(() -> webEngine.executeScript(script));
    }

    /**
     * Beállítja a Java-oldali összekötő objektumot, amelyet a JavaScript el tud majd érni.
     * Ha a HTML már betöltődött, azonnal injektálja is.
     *
     * @param connector a Java bridge objektum (pl. JavaController)
     */

    public void setJavaConnector(Object connector) {
        this.javaConnector = connector;

        if (webEngine.getLoadWorker().getState() == javafx.concurrent.Worker.State.SUCCEEDED) {
            System.out.println("[DEBUG] Bridge utólag betöltve");
            injectJavaConnector();
        }
    }

    /**
     * A Java bridge injektálása a JavaScript „window” objektumába.
     * A JavaScript így a {@code window.java} néven eléri az objektumot.
     */

    private void injectJavaConnector() {
        if (javaConnector == null) {
            System.out.println("[DEBUG] Bridge még null");
            return;
        }

        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("java", javaConnector);
        System.out.println("[DEBUG] JavaConnector a js-ben kész");
    }
    /**
     * Elindítja a térkép HTML fájljának betöltését a WebEngine segítségével.
     * A fájl relatív útvonala: {@code /html/map.html}.
     */

    public void startLoad() {
        webEngine.load(getClass().getResource("/html/map.html").toExternalForm());
    }
    /**
     * Visszaadja a használt {@link WebEngine} példányt.
     *
     * @return a WebEngine objektum
     */

    public WebEngine getWebEngine() {
        return webEngine;
    }
}