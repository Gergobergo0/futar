// --- controller/map/PopupManager.java ---
package futar.futar.controller.map;

import futar.futar.model.DepartureDTO;
import futar.futar.model.StopDTO;
import futar.futar.service.DepartureService;
import futar.futar.service.FavoriteManager;
import futar.futar.service.StopService;
import futar.futar.utils.UIUtils;
import futar.futar.view.RouteViewBuilder;
import futar.futar.view.StopViewBuilder;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * kezeli a térképen megjelenő popup ablakokat, azok frissítését, az előnézeti légvonal csík megjeleníétését
 * összekapcsolja a javaFX webview-t a JS kóddal, kezeli a kedvencek popup-ot
 * <p>
 * A JavaScript felülettel való kommunikációt a {@link MapInitializer} segítségével valósítja meg.
 */
public class PopupManager {

    final MapInitializer mapInitializer;
    private final FavoriteManager favoriteManager;
    private final StopService stopService = new StopService();
    private String lastStopId; //utoljára kiválasztott megálló azonosítója
    private String lastStopName; //utoljára kiválasztott neve
    private ScheduledExecutorService scheduler; // frissítéseket kezeli
    StopMarkerDisplayer stopMarkerDisplayer;
    private String lastPreviewFrom = ""; //utolsó "honnan" megálló előnézethez
    private String lastPreviewTo = ""; //utolsó "hová" megálló
    private final StopViewBuilder stopViewBuilder = new StopViewBuilder(); //Html nézet a megálló indulás nézethez
    private final RouteViewBuilder routeViewBuilder = new RouteViewBuilder(); //Html nézet az útvonal nézethez
    private final FavoriteHandler favoriteHandler; //Kedvenceket kezelő osztály
    private String lastTripId = null; //Utoljára kiválasztott járat azonosítója (járatnézethes)
    private final DepartureService departureService = new DepartureService();
    /**
     * Létrehozza a popup-kezelőt, betölti a kedvenceket, és beállítja a JavaScript bridge-et.
     *
     * @param mapInitializer     a WebView inicializálója
     * @param favoriteManager    kedvencek kezelője
     * @param stopMarkerDisplayer markerkezelő a térképen
     * @param favoriteHandler    a kedvencek változásait kezelő komponens
     */
    public PopupManager(MapInitializer mapInitializer, FavoriteManager favoriteManager, StopMarkerDisplayer stopMarkerDisplayer, FavoriteHandler favoriteHandler) {
        this.mapInitializer = mapInitializer;
        this.favoriteManager = favoriteManager;
        this.favoriteManager.load(); //kedvencek betöltése indításkor
        this.stopMarkerDisplayer =  stopMarkerDisplayer;
        this.favoriteHandler = favoriteHandler;

        //A js window objektumhoz és hozzájuk a Java oldali kedvenc kezelő
        JSObject window = getJsWindow();
        window.setMember("java", favoriteHandler);


    }
    /**
     * beállítja az aktív járatot és törli a kiválasztott megállókat
     *
     * @param tripId az járat azonosítója
     */
    public void setActiveTripId(String tripId)
    {
        updateSelectedStop(null, null);

        this.lastTripId = tripId;
    }
    /**
     * Megjeleníti egy megálló indulásait popupban, és elindítja az automatikus frissítést
     *
     * @param stopId a megálló azonosítója
     * @param name   a megálló neve
     * @param lat    földrajzi szélesség
     * @param lon    földrajzi hosszúság
     */
    public void showDepartures(String stopId, String name, double lat, double lon) {
        this.lastTripId = null; //előző útvonal nézet törlése
        updateSelectedStop(stopId, name);
        //UI szálon beállítjuk a kiválasztott megállót js oldalon is
        updateJsSelectedStop(stopId, name);
        //popup frissítés és automatikus újratöltés elindítása
        loadAndShowStopPopup(stopId, name, lat, lon);
        startAutoRefresh();
    }


    public StopViewBuilder getStopViewBuilder() {
        return stopViewBuilder;
    }

    public RouteViewBuilder getRouteViewBuilder() {
        return routeViewBuilder;
    }

    /**
     * frissíti a popup tartalmát a popup (trip/stop) típusa alapján
     */
    public void refreshPopupContent() {
        System.out.println("[JAVA] PopupContent frissítve");
        if (lastStopId != null && lastStopName != null) { //h
            loadAndShowStopPopup(lastStopId, lastStopName, 0, 0);
        } else if (lastTripId != null) {
            new Thread(() -> {
                try {
                    List<StopDTO> stops = stopService.getStopsByTripId(lastTripId);

                    Optional<String> routeNameOpt = departureService.getRouteNameByTripId(lastTripId);
                    Optional<String> routeTypeOpt = departureService.getRouteTypeByTripId(lastTripId); // ha van ilyen metódusod

                    String routeName = routeNameOpt.orElse("Ismeretlen járat");
                    String routeType = routeTypeOpt.orElse("BUS");
                    System.out.println("POPUPMANAGER: " + routeType);
                    String html = routeViewBuilder.build(routeName, routeType, stops);
                    Platform.runLater(() -> {
                        showPopup("Járat nézet", html);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * megjeleníti a kiválasztott megálló alapján az indulási információkat
     * @param stopId    megálló id
     * @param name      megálló név
     * @param lat       szélesség
     * @param lon       hosszúság
     */
    public void loadAndShowStopPopup(String stopId, String name, double lat, double lon) {

        updateSelectedStop(stopId, name);

        new Thread(() -> {
            try {

                List<DepartureDTO> departures = departureService.getDepartures(stopId);
                boolean isFavorite = favoriteManager.isFavoriteStop(stopId);
                String html = stopViewBuilder.build(stopId, name, departures, isFavorite);

                Platform.runLater(() -> {
                    mapInitializer.executeScript("showFloatingPopup('Megálló nézet', '" +
                            //"Megálló nézet" + "', '" +
                            UIUtils.escapeJs(html) + "')");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     */
    /**
     * Megjeleníti a popupot a megadott HTML tartalommal
     * @param title popup címe
     * @param htmlContent HTML tartalom
     */
    public void showPopup(String title, String htmlContent) {
        startAutoRefresh(); //frissítés indítása
        String escapedHtml = UIUtils.escapeJs(htmlContent);
        String escapedTitle = UIUtils.escapeJs(title);

        mapInitializer.executeScript("showFloatingPopup('" + escapedTitle + "', '" + escapedHtml + "')"); //js-ben meghívja a
    }


    /**
     * Elindítja az automatikus frissítést 30 másodpercenként
     */
    protected void startAutoRefresh() {
        stopAutoRefresh();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::refreshPopupContent), 30, 30, TimeUnit.SECONDS);
    }
    /**
     * Leállítja az automatikus popup frissítést
     */
    public void stopAutoRefresh() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }


    public String getLastStopId() {
        return lastStopId;
    }

    public String getLastStopName() {
        return lastStopName;
    }


    /**
     * Elindítja a popup újratöltését a JavaFX UI szálon.
     */
    public void notifyPopupRefresh() {
        Platform.runLater(this::refreshPopupContent);
    }

    public FavoriteManager getFavoriteManager() {
        return favoriteManager;
    }

    /**
     * Megjeleníti a két pont közti légvonalat térképen.
     */
    public void showPlannedRoute(double fromLat, double fromLng, double toLat, double toLng, double distanceKm) {
        Platform.runLater(() -> {
            JSObject window = getJsWindow();
            window.call("showRouteLine", fromLat, fromLng, toLat, toLng, String.format("%.2f km", distanceKm));
        });
    }

    /**
     * Automatikus előnézet útvonalhoz, ha a "Honnan" és "Hová" meg van adva
     */
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

                Platform.runLater(() -> {
                    stopMarkerDisplayer.clearMap(); // Ez hívja meg a WebEngine-et
                    showPlannedRoute(from.getLat(), from.getLon(), to.getLat(), to.getLon(), distanceKm);
                });
            }
        }).start();
    }




    /**
     */
    /**
     * Kiszámítja a két pont közti távolságot földrajzi koordináták alapján (Haversine-formula).
     * @param lat1  kiinduló szélesség
     * @param lon1  kiinduló hosszúság
     * @param lat2  cél szélesség
     * @param lon2  cél hosszúság
     * @return
     */
    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; //föld radiusa (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Törli a térképen megjelenített útvonal előnézetet.
     */
    public void clearRoutePreview() {
        Platform.runLater(() -> {
            JSObject window = getJsWindow();
            window.call("clearRouteLine");
        });
    }


    /**
     * eltávolítja a popupot
     */
    public void clearFloatingPopup() {
                mapInitializer.executeScript("""
            var popup = document.getElementById("floating-popup");
            if (popup) popup.remove();
            """);

    }

    /**
     * frissíti a kiválasztott megálló adatait, átadja a favoriteHandler-nek is
     * @param stopId    megálló id
     * @param name      megálló név
     */
    public void updateSelectedStop(String stopId, String name) {
        this.lastStopId = stopId;
        this.lastStopName = name;
        favoriteHandler.setSelectedStop(stopId, name);
    }

    /**
     * Beállítja a js oldalon a kiválasztott megálló adatait
     * @param stopId megálló azonosítója
     * @param name megálló neve
     */
    private void updateJsSelectedStop(String stopId, String name) {
        Platform.runLater(() -> {
            JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
            window.setMember("selectedStopId", stopId);
            window.setMember("selectedStopName", name);
        });
    }

    private JSObject getJsWindow() {
        return (JSObject) mapInitializer.getWebEngine().executeScript("window");
    }



}
