package futar.futar.controller.map;

import futar.futar.api.TripApi;
import futar.futar.model.StopDTO;
import futar.futar.service.DepartureService;
import futar.futar.view.RouteViewBuilder;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.openapitools.client.api.DefaultApi;

import java.util.List;
import java.util.Optional;
/**
 * Felelős egy adott járat útvonalának (megállóinak) megjelenítéséért a lebegő popupban.
 * <p>
 * A {@link PopupManager} segítségével HTML tartalmat épít és jelenít meg.
 */
public class RouteInfoDisplayer {
    /**
     * A popupok kezeléséért felelős komponens.
     */

    private final PopupManager popupManager;
    /**
     * A járatnév lekérdezését végző szolgáltatás.
     */

    private final DepartureService departureService;
    /**
     * Létrehozza az útvonalmegjelenítő példányt.
     *
     * @param popupManager a popupok kezeléséért felelős osztály
     */

    public RouteInfoDisplayer(PopupManager popupManager) {
        this.popupManager = popupManager;
        this.departureService = new DepartureService();
    }
    /**
     * Lekéri a járat összes megállóját, összeállítja a HTML-t és megjeleníti a popupot.
     * <p>
     * A háttérszálban történik az adatok betöltése, és a JavaFX UI szálon a megjelenítés.
     *
     * @param tripId a lekérdezendő járat azonosítója
     */

    public void displayRouteInfo(String tripId) {
        new Thread(() -> {
            try {
                TripApi tripApi = new TripApi();
                List<StopDTO> stops = tripApi.getStopsByTrip(tripId);
                if (stops.isEmpty()) return;

                Optional<String> routeNameOpt = departureService.getRouteNameByTripId(tripId);
                String routeName = routeNameOpt.orElse("Ismeretlen járat");

                String html = popupManager.getRouteViewBuilder().build(routeName, stops);
                popupManager.setActiveTripId(tripId);
                popupManager.startAutoRefresh(); // Indítsd itt is!

                Platform.runLater(() -> {
                    popupManager.clearFloatingPopup();
                    popupManager.showFloatingPopup(routeName, html);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}
