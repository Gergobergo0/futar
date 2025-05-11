package futar.futar.controller.map;

import futar.futar.model.DepartureDTO;
import futar.futar.service.DepartureService;
import futar.futar.view.StopViewBuilder;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.openapitools.client.api.DefaultApi;

import java.util.List;
/**
 * Adott megállók információaiért felelős osztály
 * <p>
 * Az adatokat a {@link DepartureService}-en keresztül tölti be, majd a {@link PopupManager}
 * segítségével jeleníti meg lebegő HTML nézetként
 */

public class StopInfoDisplayer {

    private final PopupManager popupManager;
    /**
     * Indulási adatok lekérdezését végző service
     */
    private final DepartureService departureService;
    /**
     * Létrehozza a StopInfoDisplayer példányt
     *
     * @param popupManager a popupok kezeléséért felelős osztály
     */
    public StopInfoDisplayer(PopupManager popupManager) {
        this.popupManager = popupManager;
        this.departureService = new DepartureService();
    }
    /**
     * Lekéri az adott megállóhoz tartozó indulásokat, és megjeleníti popupként a térképen
     * A lekérdezés külön szálon fut,a megjelenítés UI szálon történik
     *
     * @param stopId   a megálló azonosítója
     * @param stopName a megálló neve
     */

    public void displayStopInfo(String stopId, String stopName) {
        popupManager.clearFloatingPopup();

        new Thread(() -> {
            try {
                List<DepartureDTO> departures = departureService.getDepartures(stopId);
                if (departures == null || departures.isEmpty()) return;
                boolean isFavorite = popupManager.getFavoriteManager().isFavoriteStop(stopId);
                String html = popupManager.getStopViewBuilder().build(stopId, stopName, departures, isFavorite);

                Platform.runLater(() -> {
                    popupManager.showPopup("Megálló nézet", html);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



}
