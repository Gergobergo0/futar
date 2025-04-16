package futar.futar.controller;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.DepartureDTO;
import futar.futar.model.StopDTO;
import futar.futar.service.DepartureService;
import futar.futar.service.StopService;
import futar.futar.view.DepartureViewBuilder;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.util.List;

public class MapController {

    private final WebEngine webEngine;
    private final StopService stopService = new StopService();

    public MapController(javafx.scene.web.WebView mapView) {
        this.webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/html/map.html").toExternalForm());
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");

                // EDDIG: window.setMember("java", this);
                // HELYETTE:
                if (javaConnector != null) {
                    window.setMember("java", javaConnector);
                    System.out.println("Java objektum átadva a JavaScript-nek");
                } else {
                    System.out.println("❌ Java objektum nincs beállítva");
                }
            }
        });

    }

    public void logFromJavaScript(String message) {
        System.out.println("Received from JS: " + message);
    }

    public void handleStopDetails(String stopId, String name, double lat, double lon) {
        new Thread(() -> {
            try {
                List<DepartureDTO> departures = new DepartureService(new org.openapitools.client.api.DefaultApi(ApiClientProvider.getClient()))
                        .getDepartures(stopId);

                if (departures.isEmpty()) {
                    departures = new DepartureService(new org.openapitools.client.api.DefaultApi(ApiClientProvider.getClient()))
                            .getNearbyDepartures(lat, lon);
                    System.out.println("Üres");
                }

                String popupHtml = DepartureViewBuilder.build(departures);

                Platform.runLater(() -> {
                    System.out.println("Calling updatePopupContent JS");
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.call("updatePopupContent", name, popupHtml);
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private Object javaConnector;

    public void setJavaConnector(Object connector) {
        this.javaConnector = connector;
    }
    public void showMultipleStopsOnMap(List<StopDTO> stops) {
        System.out.println("showMultipleStops meghívódik");
        if (stops == null || stops.isEmpty()) return;

        clearAndShowStops(stops);
        StopDTO first = stops.get(0);
        System.out.println("Zoomolás: " + first.getLat() + ", " + first.getLon()); // debug

        // FONTOS: ezt Platform.runLater()-ből hívd, mert külön thread-en futunk!
        Platform.runLater(() -> {
            System.out.println("Zoomolás: " + first.getLat() + ", " + first.getLon()); // debug
            webEngine.executeScript(String.format("focusOn(%f, %f)", first.getLat(), first.getLon()));
        });
    }




    public void clearAndShowStops(List<StopDTO> stops) {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.call("clearStops");
        for (StopDTO stop : stops) {
            window.call("addStopTracked", stop.getLat(), stop.getLon(), stop.getName(), stop.getId());
        }

    }
}
