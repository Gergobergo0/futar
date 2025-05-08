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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * Kezeli a lebegő popup ablakokat, azok tartalmának dinamikus frissítését, valamint az előnézeti útvonalakat.
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

        //A js 'window' objektumhoz és hozzájuk a Java oldali kedvenc kezelő
        JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
        window.setMember("java", favoriteHandler);


    }
    /**
     * Beállítja az aktív járatot, törli az előző stop adatokat.
     *
     * @param tripId az útvonal/járat azonosítója
     */
    public void setActiveTripId(String tripId)
    {
        this.lastStopId = null;
        this.lastStopName = null;

        this.lastTripId = tripId;
    }
    /**
     * Megjeleníti egy megálló indulásait popupban, és elindítja az automatikus frissítést.
     *
     * @param stopId az érintett megálló azonosítója
     * @param name   a megálló neve
     * @param lat    földrajzi szélesség
     * @param lon    földrajzi hosszúság
     */

    public void showDepartures(String stopId, String name, double lat, double lon) {
        this.lastTripId = null; //előző útvonal nézet törlése
        this.lastStopId = stopId; //aktuális megálló ID
        this.lastStopName = name;
        favoriteHandler.setSelectedStop(stopId, name); //kiválasztott kedvencek frissítése
        //UI szálon beállítjuk a kiválasztott megállót js oldalon is
        Platform.runLater(() -> {
            JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
            window.setMember("selectedStopId", stopId);
            window.setMember("selectedStopName", name);
        });
        //popup frissítés és automatikus újratöltés elindítása
        fetchAndUpdatePopup(stopId, name, lat, lon);
        startAutoRefresh();
    }


    public StopViewBuilder getStopViewBuilder() {
        return stopViewBuilder;
    }

    public RouteViewBuilder getRouteViewBuilder() {
        return routeViewBuilder;
    }

    /**
     * Újratölti a jelenlegi popup tartalmát: indulásokat vagy útvonalat.
     */

    public void refreshPopupContent() {
        System.out.println("[JAVA] PopupContent frissítve");
        if (lastStopId != null && lastStopName != null) { //h
            fetchAndUpdatePopup(lastStopId, lastStopName, 0, 0);
        } else if (lastTripId != null) {
            new Thread(() -> {
                try {
                    List<StopDTO> stops = stopService.getStopsByTripId(lastTripId);
                    String routeName = new DepartureService().getRouteNameByTripId(lastTripId).orElse("Ismeretlen");
                    String html = routeViewBuilder.build(routeName, stops);

                    Platform.runLater(() -> {
                        showFloatingPopup(routeName, html);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Lekéri az adott megálló indulásait, felépíti a HTML-t, majd megjeleníti popupban.
     */

    private void fetchAndUpdatePopup(String stopId, String name, double lat, double lon) {
        new Thread(() -> {
            try {
                DepartureService service = new DepartureService();
                List<DepartureDTO> departures = service.getDepartures(stopId); //indulások

                boolean isFavorite = favoriteManager.isFavoriteStop(stopId); //megálló kedvenc-e?
                String html = stopViewBuilder.build(stopId, name, departures, isFavorite); //Html generálás

                Platform.runLater(() -> {
                    //popup js oldalon
                    mapInitializer.executeScript("showFloatingPopup('" +
                            UIUtils.escapeJs(name) + "', '" +
                            UIUtils.escapeJs(html) + "')");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
    /**
     * Megjeleníti a lebegő popupot a megadott HTML tartalommal.
     */

    public void showFloatingPopup(String title, String htmlContent) {
        startAutoRefresh(); //frissítés indítása
        String escapedHtml = UIUtils.escapeJs(htmlContent); //html escape js-hez
        String escapedTitle = UIUtils.escapeJs(title); //cím escape js-hez

        mapInitializer.executeScript("showFloatingPopup('" + escapedTitle + "', '" + escapedHtml + "')"); //js-ben meghívja a popup megjelenítést
    }


    /**
     * Elindítja az automatikus frissítést 30 másodpercenként.
     */

    protected void startAutoRefresh() {
        stopAutoRefresh();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::refreshPopupContent), 30, 30, TimeUnit.SECONDS);
    }
    /**
     * Leállítja az automatikus popup frissítést.
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

    public void setSelectedStop(String id, String name) {
        this.lastStopId = id;
        this.lastStopName = name;
    }
    /**
     * Elindítja a popup újratöltését a JavaFX UI szálon.
     */
    public void notifyPopupRefreshNeeded() {
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
            JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
            window.call("showRouteLine", fromLat, fromLng, toLat, toLng, String.format("%.2f km", distanceKm));
        });
    }

    /**
     * Automatikus előnézet útvonalhoz, ha a "Honnan" és "Hová" meg van adva.
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
     * Popup megjelenítése egy konkrét megállóra, előre betöltött adatokkal.
     */
    public void showFloatingPopupForStop(String stopId, String name, double lat, double lon) {
        System.out.println("[JAVA] showFloatingPopupForStop - stopId: " + stopId + ", name: " + name);

        this.lastStopId = stopId;
        this.lastStopName = name;
        setSelectedStop(stopId, name); // 💥 EZ HIÁNYZOTT
        favoriteHandler.setSelectedStop(stopId, name); // 🟢 Megvan


        DepartureService service = new DepartureService();
        List<DepartureDTO> departures = service.getDepartures(stopId);
        boolean isFavorite = favoriteManager.isFavoriteStop(stopId);

        String html = stopViewBuilder.build(stopId, name, departures, isFavorite);

        Platform.runLater(() -> {
            mapInitializer.executeScript("showFloatingPopup('" +
                    UIUtils.escapeJs(name) + "', '" +
                    UIUtils.escapeJs(html) + "')");
        });
    }



    /**
     * Kiszámítja a két pont közti távolságot földrajzi koordináták alapján (Haversine-formula).
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
            JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
            window.call("clearRouteLine");
        });
    }

    /**
     * Eltávolítja a lebegő popup DOM elemét.
     */

    public void clearFloatingPopup() {
                mapInitializer.executeScript("""
            var popup = document.getElementById("floating-popup");
            if (popup) popup.remove();
            """);

    }


}
