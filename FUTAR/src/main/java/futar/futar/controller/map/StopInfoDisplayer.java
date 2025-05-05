package futar.futar.controller.map;

import futar.futar.model.DepartureDTO;
import futar.futar.service.DepartureService;
import futar.futar.view.StopViewBuilder;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.openapitools.client.api.DefaultApi;

import java.util.List;

public class StopInfoDisplayer {
    private final PopupManager popupManager;
    private final DepartureService departureService;

    public StopInfoDisplayer(PopupManager popupManager) {
        this.popupManager = popupManager;
        this.departureService = new DepartureService(new DefaultApi());
    }

    public void displayStopInfo(String stopId, String stopName, double lat, double lon) {
        popupManager.clearFloatingPopup();

        new Thread(() -> {
            try {
                List<DepartureDTO> departures = departureService.getDeparturesForStop(stopId, lat, lon);
                if (departures == null || departures.isEmpty()) return;

                String html = StopViewBuilder.buildFloatingPopup(stopName, departures);

                String escapedTitle = escapeJs(stopName);
                String escapedHtml = escapeJs(html);

                Platform.runLater(() -> {
                    JSObject window = (JSObject) popupManager.getWebEngine().executeScript("window");
                    window.call("showFloatingPopup", escapedTitle, escapedHtml);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String escapeJs(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "");
    }
}
