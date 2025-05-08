package futar.futar.controller.map;

import javafx.scene.web.WebView;
import futar.futar.service.StopService;
import futar.futar.service.FavoriteManager;
import futar.futar.model.FavoriteStop;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * Térképes felület vezérlője
 * <p>
 *     Ez az osztály vezérli a térkép inicializálását, markerezést, popupokat, kedvencek hozzáadását, eltávolítását
 * </p>
 */
public class MapController {
    private final MapInitializer mapInitializer;
    private final FavoriteHandler favoriteHandler;
    public final PopupManager popupManager;
    public final RouteInfoDisplayer routeInfoDisplayer;
    private final StopMarkerDisplayer stopMarkerDisplayer;
    public final StopInfoDisplayer stopInfoDisplayer;

    /**
     * létrehoz egy új kontroller példányt és létrehozza a kapcsolódó osztályokat
     * @param mapView a WebView elem, amely a HTML-alapú térképet tartalmazza
     */
    public MapController(WebView mapView) {
        FavoriteManager favoriteManager = FavoriteManager.getInstance();
        favoriteManager.load();
        this.mapInitializer = new MapInitializer(mapView);
        this.favoriteHandler = new FavoriteHandler(favoriteManager);
        this.stopMarkerDisplayer = new StopMarkerDisplayer(mapInitializer);
        this.popupManager = new PopupManager(mapInitializer, favoriteManager, stopMarkerDisplayer, favoriteHandler);
        favoriteHandler.setRefreshCallback(() -> popupManager.notifyPopupRefreshNeeded());

        this.routeInfoDisplayer = new RouteInfoDisplayer(popupManager);
        stopInfoDisplayer = new StopInfoDisplayer(popupManager);






    }

    /**
     * elindítja a térkép betöltését (ha megszakad az internet)
     */
    public void finishInit() {
        mapInitializer.startLoad(); // később tölti be a html-t (internetkapcsolat ellenőrzés után)
    }

    /**
     * getter
     * @return
     */
    public PopupManager getPopupManager() {
        return popupManager;
    }

    /**
     * beállítja a java összekötő objektumot , ami a js hívásokért és fogadásokért felelős
     * @param javaConnector
     */
    public void setJavaConnector(Object javaConnector) {
        mapInitializer.setJavaConnector(javaConnector);
    }


    /**
     * A megadott megálló közvetlen törlése vagy eltávolítása
     * frissíti a popupot
     * @param stopId megálló Id
     * @param stopName  megálló neve
     */
    public void toggleFavoriteDirect(String stopId, String stopName) {
        if (stopId == null || stopName == null) {
            System.out.println("[HIBA] stopId vagy stopName null");
            return;
        }

        popupManager.updateSelectedStop(stopId, stopName);
        favoriteHandler.setSelectedStop(stopId, stopName);
        favoriteHandler.toggleFavorite();

        popupManager.notifyPopupRefreshNeeded(); // UI újrarajzolás
    }


    /**
     * A popupból menti a a kedvenc megállót
     */
    public void addFavoriteStop() {
        String stopId = popupManager.getLastStopId();
        String stopName = popupManager.getLastStopName();

        if (stopId == null || stopName == null) return;

        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Név megadása");
            dialog.setHeaderText("Adj nevet a kedvencnek");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                favoriteHandler.getFavoriteManager().addStop(new FavoriteStop(name, stopId, stopName));
                System.out.println("Kedvencként mentve: " + name);
            });
        });
    }


    /**
     * Átváltja az aktuális megállót kedvenccé vagy eltávolítja onnan, a popup alapján
     */
    public void toggleFavorite() {
        System.out.println("[DEBUG] toggleFavorite() → stopId: " + popupManager.getLastStopId() + ", stopName: " + popupManager.getLastStopName());
        favoriteHandler.setSelectedStop(
                popupManager.getLastStopId(),
                popupManager.getLastStopName()
        );

        if (popupManager == null) return;

        String stopId = popupManager.getLastStopId();
        String stopName = popupManager.getLastStopName();


        if (stopId == null || stopName == null) {
            System.out.println("[HIBA] MapController.toggleFavorite(): Nincs stopId vagy stopName");
            return;
        }

        favoriteHandler.setSelectedStop(stopId, stopName);
        favoriteHandler.toggleFavorite();

        // Frissítjük a popupot is, hogy megjelenjen az új gombszöveg
    }





    public void handleRouteClick(String tripId) {
        routeInfoDisplayer.displayRouteInfo(tripId);
    }

    public FavoriteManager getFavoriteManager() {
        return favoriteHandler.getFavoriteManager();
    }

    public StopMarkerDisplayer getStopMarkerDisplayer() {
        return stopMarkerDisplayer;
    }
}