package futar.futar.controller.map;

import futar.futar.model.StopDTO;
import javafx.application.Platform;
import netscape.javascript.JSObject;

import java.util.List;

public class StopMarkerDisplayer {
    private final MapInitializer mapInitializer;

    public StopMarkerDisplayer(MapInitializer mapInitializer) {
        this.mapInitializer = mapInitializer;
    }

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
}