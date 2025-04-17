package futar.futar.controller.map;

import futar.futar.api.TripApi;
import futar.futar.model.StopDTO;
import futar.futar.view.RouteViewBuilder;
import javafx.application.Platform;
import netscape.javascript.JSObject;

import java.util.List;

public class RouteInfoDisplayer {
    private final PopupManager popupManager;

    public RouteInfoDisplayer(PopupManager popupManager) {
        this.popupManager = popupManager;
    }

    public void displayRouteInfo(String tripId) {
        new Thread(() -> {
            try {
                TripApi tripApi = new TripApi();
                List<StopDTO> stops = tripApi.getStopsByTrip(tripId);
                if (stops.isEmpty()) return;

                String html = RouteViewBuilder.build("Járat " + tripId, stops);

                Platform.runLater(() -> {
                    JSObject window = (JSObject) popupManager.mapInitializer.getWebEngine().executeScript("window");
                    window.call("updatePopupContent", "Járat " + tripId, html);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
