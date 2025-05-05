// --- controller/map/PopupManager.java ---
package futar.futar.controller.map;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.DepartureDTO;
import futar.futar.model.StopDTO;
import futar.futar.service.DepartureService;
import futar.futar.service.FavoriteManager;
import futar.futar.service.StopService;
import futar.futar.view.DepartureViewBuilder;
import futar.futar.view.StopViewBuilder;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.openapitools.client.api.DefaultApi;
import futar.futar.controller.map.StopMarkerDisplayer;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PopupManager {
    final MapInitializer mapInitializer;
    private final FavoriteManager favoriteManager;
    private final StopService stopService = new StopService();
    private String lastStopId;
    private String lastStopName;
    private ScheduledExecutorService scheduler; // 🔁 háttérfrissítés kezelő
    StopMarkerDisplayer stopMarkerDisplayer;
    private String lastPreviewFrom = "";
    private String lastPreviewTo = "";

    public PopupManager(MapInitializer mapInitializer, FavoriteManager favoriteManager, StopMarkerDisplayer stopMarkerDisplayer) {
        this.mapInitializer = mapInitializer;
        this.favoriteManager = favoriteManager;
        this.favoriteManager.load(); // 🔄 Kedvencek betöltése indításkor
        this.stopMarkerDisplayer =  stopMarkerDisplayer;
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
        startAutoRefresh();
    }

    public void refreshPopupContent() {
        if (lastStopId == null || lastStopName == null) return;

        fetchAndUpdatePopup(lastStopId, lastStopName, 0, 0);
    }

    private void fetchAndUpdatePopup(String stopId, String name, double lat, double lon) {
        /*
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
                final boolean isFavorite = favoriteManager.isFavoriteStop(stopId);

                Platform.runLater(() -> updatePopup(stopNameFinal, stopIdFinal, departuresFinal, isFavorite));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/
        DepartureService service = new DepartureService(new DefaultApi(ApiClientProvider.getClient()));

        List<DepartureDTO> departures = service.getDepartures(stopId);
        String html = StopViewBuilder.buildFloatingPopup(name, departures);

        Platform.runLater(() -> {
            mapInitializer.executeScript("showFloatingPopup('" +
                    escapeJs(name) + "', '" +
                    escapeJs(html) + "')");
        });

    }

    public void showFloatingPopup(String title, String htmlContent) {
        String escapedHtml = escapeJs(htmlContent);
        String escapedTitle = escapeJs(title);

        mapInitializer.executeScript("showFloatingPopup('" + escapedTitle + "', '" + escapedHtml + "')");
    }


    private String escapeJs(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "");
    }

    private void updatePopup(String stopName, String stopId, List<DepartureDTO> departures, boolean isFavorite) {
        String buttonText = isFavorite ? "Törlés a kedvencekből" : "Kedvenc";

        String html = DepartureViewBuilder.build(departures, buttonText);

        JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
        window.call("updatePopupContent", stopName, html);
    }

    private void startAutoRefresh() {
        stopAutoRefresh();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::refreshPopupContent), 30, 30, TimeUnit.SECONDS);
    }

    public void stopAutoRefresh() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    public void stopPopupRefresh() {
        stopAutoRefresh();
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

    // 👇 Útvonal megjelenítése a térképen
    public void showPlannedRoute(double fromLat, double fromLng, double toLat, double toLng, double distanceKm) {
        Platform.runLater(() -> {
            JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
            window.call("showRouteLine", fromLat, fromLng, toLat, toLng, String.format("%.2f km", distanceKm));
        });
    }

    // 🔄 Automatikus előnézet légvonalas összekötéssel (ha mindkettő meg van adva)
    public void tryShowRoutePreview(String fromName, String toName) {
        if (fromName == null || toName == null || fromName.isBlank() || toName.isBlank()) return;

        if (fromName.equals(lastPreviewFrom) && toName.equals(lastPreviewTo)) return;
        lastPreviewFrom = fromName;
        lastPreviewTo = toName;

        new Thread(() -> {
            StopDTO from = stopService.getStopByName(fromName);
            StopDTO to = stopService.getStopByName(toName);

            if (from != null && to != null) {
                double distanceKm = calculateDistanceKm(
                        from.getLat(), from.getLon(),
                        to.getLat(), to.getLon()
                );

                // ⬇️ Biztosítsuk, hogy csak az FX szál használja a WebEngine-et
                Platform.runLater(() -> {
                    stopMarkerDisplayer.clearMap(); // Ez hívja meg a WebEngine-et
                    showPlannedRoute(from.getLat(), from.getLon(), to.getLat(), to.getLon(), distanceKm);
                });
            }
        }).start();
    }

    public void showFloatingPopupForStop(String stopId, String name, double lat, double lon) {
        DepartureService service = new DepartureService(new DefaultApi(ApiClientProvider.getClient()));
        List<DepartureDTO> departures = service.getDepartures(stopId);

        String html = StopViewBuilder.buildFloatingPopup(name, departures);

        Platform.runLater(() -> {
            mapInitializer.executeScript("showFloatingPopup('" +
                    escapeJs(name) + "', '" +
                    escapeJs(html) + "')");
        });
    }



    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    public void clearRoutePreview() {
        Platform.runLater(() -> {
            JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
            window.call("clearRouteLine");
        });
    }

    public WebEngine getWebEngine() {
        return mapInitializer.getWebEngine();
    }

    public void clearFloatingPopup() {
        mapInitializer.executeScript("""
        const oldPopup = document.getElementById("floating-popup");
        if (oldPopup) oldPopup.remove();
    """);
    }


}
