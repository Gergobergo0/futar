package futar.futar.controller.map;

import futar.futar.model.StopDTO;
import javafx.application.Platform;
import netscape.javascript.JSObject;

import java.util.List;
/**
 * Felelős a megállók térképen való megjelenítéséért (marker elhelyezése), valamint azok törléséért.
 * <p>
 * A JavaScript oldalhoz a {@link MapInitializer} segítségével kapcsolódik.
 */

public class StopMarkerDisplayer {
    /**
     * A WebView motor, amely lehetővé teszi Java ↔ JavaScript kommunikációt.
     */

    private final MapInitializer mapInitializer;
    /**
     * Létrehozza a markermegjelenítőt a térképen való JavaScript műveletekhez.
     *
     * @param mapInitializer a WebEngine inicializálója
     */

    public StopMarkerDisplayer(MapInitializer mapInitializer) {
        this.mapInitializer = mapInitializer;
    }
    /**
     * Több megállót jelenít meg a térképen, és ha szükséges, az első megállóra popupot is nyit.
     *
     * @param stops     a megjelenítendő megállók listája
     * @param openPopup ha {@code true}, az első megállóra automatikusan nyit egy popupot
     */

    public void showMultipleStops(List<StopDTO> stops, boolean openPopup) {
        if (stops == null || stops.isEmpty()) return;

        JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
        window.call("clearStops");

        for (StopDTO stop : stops) {
            window.call("addStopTracked", stop.getLat(), stop.getLon(), stop.getName(), stop.getId());
        }

        StopDTO first = stops.get(0);

        Platform.runLater(() -> {
            mapInitializer.getWebEngine().executeScript(String.format("focusOn(%f, %f)", first.getLat(), first.getLon()));

            if (openPopup) {
                String script = String.format("""
                    var targetMarker = allStops.find(m =>
                        m.getLatLng().lat === %f && m.getLatLng().lng === %f
                    );
                    if (targetMarker) {
                        if (selectedMarker && selectedMarker !== targetMarker) {
                            selectedMarker.setIcon(defaultIcon);
                        }
                        selectedMarker = targetMarker;
                        selectedMarker.setIcon(highlightedIcon);
                        selectedMarker.openPopup(); 
                    }
                """, first.getLat(), first.getLon());
                mapInitializer.getWebEngine().executeScript(script);
            }
        });
    }

    /**
     * Törli a térképen megjelenített megállókat és útvonalakat.
     */

    public void clearMap() {
        JSObject window = (JSObject) mapInitializer.getWebEngine().executeScript("window");
        window.call("clearStops");
        window.call("clearRouteLine");
    }
}