package futar.futar.controller.map;

import javafx.scene.web.WebView;
import futar.futar.service.StopService;
import futar.futar.service.FavoriteManager;
import futar.futar.model.FavoriteStop;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;


public class MapController {
    private final MapInitializer mapInitializer;
    private final FavoriteHandler favoriteHandler;
    public final PopupManager popupManager;
    public static RouteInfoDisplayer routeInfoDisplayer;
    private final StopMarkerDisplayer stopMarkerDisplayer;
    public static StopInfoDisplayer stopInfoDisplayer;

    public MapController(WebView mapView) {
        StopService stopService = new StopService();
        FavoriteManager favoriteManager = FavoriteManager.getInstance(); // ✅
        favoriteManager.load();
        this.mapInitializer = new MapInitializer(mapView);
        this.favoriteHandler = new FavoriteHandler(favoriteManager);
        this.stopMarkerDisplayer = new StopMarkerDisplayer(mapInitializer);
        this.popupManager = new PopupManager(mapInitializer, favoriteManager, stopMarkerDisplayer);
        this.routeInfoDisplayer = new RouteInfoDisplayer(popupManager);
        stopInfoDisplayer = new StopInfoDisplayer(popupManager);






    }
    public void finishInit() {
        mapInitializer.startLoad(); // <-- csak most töltsd be a HTML-t
    }

    public PopupManager getPopupManager() {
        return popupManager;
    }


    public void setJavaConnector(Object javaConnector) {
        mapInitializer.setJavaConnector(javaConnector);
    }



    public void logFromJavaScript(String message) {
        System.out.println("JS → JAVA: " + message);
    }


    public void handleStopDetails(String stopId, String name, double lat, double lon) {
        System.out.println("💬 MapController.handleStopDetails meghívva: " + stopId + ", " + name);
        popupManager.setSelectedStop(stopId, name);
        favoriteHandler.setSelectedStop(stopId, name);

        // Itt marker nélkül érkezik, ne használj selectedMarker logikát
        popupManager.showFloatingPopupForStop(stopId, name, lat, lon);
    }


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
                System.out.println("✅ Kedvencként mentve: " + name);
            });
        });
    }

    public void toggleFavorite() {
        favoriteHandler.toggleFavorite();
        popupManager.notifyPopupRefreshNeeded();
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