package futar.futar.utils;

import futar.futar.controller.map.MapController;
import javafx.application.Platform;

public class JavaConnector {
    private final MapController mapController;

    public JavaConnector(MapController mapController) {
        this.mapController = mapController;
    }

    // Hívás JS-ből, ha egy megállóra kattintanak
    public void javaGetStopDetails(String stopId, String stopName) {
        System.out.println("[DEBUG] JavaConnector.javaGetStopDetails(" + stopId + ", " +stopName+")");;
        Platform.runLater(() -> {
            MapController.stopInfoDisplayer.displayStopInfo(stopId, stopName);
        });
    }


    // Hívás JS-ből, ha egy járatra kattintanak
    public void handleRouteClick(String tripId) {
        Platform.runLater(() -> mapController.handleRouteClick(tripId));
    }

    // Kedvencek gomb kezelése a popupban
    public void toggleFavorite() {
        Platform.runLater(mapController::toggleFavorite);
    }

    // JS-ből jövő naplóüzenet
    public void javaLog(String message) {
        System.out.println("[JS] " + message);
    }

    // Ha popup bezárul
    public void onPopupClosed() {
        Platform.runLater(() -> mapController.getPopupManager().stopAutoRefresh());
    }

    public void toggleFavoriteFromPopup(String stopId, String stopName) {
        System.out.println("[Bridge] toggleFavoriteFromPopup: " + stopId + " / " + stopName);
        Platform.runLater(() -> mapController.toggleFavoriteDirect(stopId, stopName));
    }

}
