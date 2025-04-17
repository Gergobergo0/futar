// --- controller/map/PopupManager.java ---
package futar.futar.controller.map;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.DepartureDTO;
import futar.futar.service.DepartureService;
import futar.futar.service.FavoriteManager;
import futar.futar.view.DepartureViewBuilder;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.openapitools.client.api.DefaultApi;

import java.util.List;

public class PopupManager {
    final MapInitializer mapInitializer;
    private final FavoriteManager favoriteManager;
    private String lastStopId;
    private String lastStopName;

    public PopupManager(MapInitializer mapInitializer, FavoriteManager favoriteManager) {
        this.mapInitializer = mapInitializer;
        this.favoriteManager = favoriteManager;
    }

    public void showDepartures(String stopId, String name, double lat, double lon) {
        this.lastStopId = stopId;
        this.lastStopName = name;

        Platform.runLater(() -> {
            JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
            window.setMember("selectedStopId", stopId);
            window.setMember("selectedStopName", name);
        });

        fetchAndUpdatePopup(stopId, name, lat, lon);
    }

    public void refreshPopupContent() {
        if (lastStopId == null || lastStopName == null) return;

        fetchAndUpdatePopup(lastStopId, lastStopName, 0, 0);
    }

    private void fetchAndUpdatePopup(String stopId, String name, double lat, double lon) {
        new Thread(() -> {
            try {
                DepartureService service = new DepartureService(new DefaultApi(ApiClientProvider.getClient()));
                List<DepartureDTO> departures = service.getDepartures(stopId);

                if (departures.isEmpty()) {
                    departures = service.getNearbyDepartures(lat, lon);
                }

                final String stopIdFinal = stopId;
                final String stopNameFinal = name;
                final List<DepartureDTO> departuresFinal = departures;

                Platform.runLater(() -> updatePopupAndCloseIfNeeded(stopNameFinal, stopIdFinal, departuresFinal));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updatePopupAndCloseIfNeeded(String stopName, String stopId, List<DepartureDTO> departures) {
        String buttonText = favoriteManager.isFavoriteStop(stopId)
                ? "Törlés a kedvencekből" : "Kedvenc";

        String html = DepartureViewBuilder.build(departures, buttonText);

        JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
        window.call("updatePopupContent", stopName, html);

        // Ha most adtuk hozzá, zárjuk be
        if (buttonText.equals("Törlés a kedvencekből")) {
            window.call("closePopup");
        }
    }

    public String getLastStopId() {
        return lastStopId;
    }

    public String getLastStopName() {
        return lastStopName;
    }

    public void setSelectedStop(String id, String name) {
        this.lastStopId = id;
        this.lastStopName = name;
    }

    public void notifyPopupRefreshNeeded() {
        Platform.runLater(this::refreshPopupContent);
    }

    public FavoriteManager getFavoriteManager() {
        return favoriteManager;
    }
}