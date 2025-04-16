package futar.futar.controller;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.DepartureDTO;
import futar.futar.model.StopDTO;
import futar.futar.service.DepartureService;
import futar.futar.service.StopService;
import futar.futar.view.DepartureViewBuilder;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.openapitools.client.api.DefaultApi;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainViewController {

    @FXML private WebView mapView;
    @FXML private TextField searchField;

    private WebEngine webEngine;
    private final StopService stopService = new StopService();
    private final ContextMenu suggestionMenu = new ContextMenu();
    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

    @FXML
    public void initialize() {
        setupMap();
        setupSearchField();
    }

    private void setupMap() {
        webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/html/map.html").toExternalForm());
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", this);
                System.out.println("Java objektum átadva a JavaScript-nek");
            }
        });
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() < 2) {
                suggestionMenu.hide();
                debounce.stop();
                return;
            }

            debounce.setOnFinished(e -> fetchSuggestions(newText));
            debounce.playFromStart();
        });

    }

    private void showSuggestions(Map<String, List<StopDTO>> grouped) {
        suggestionMenu.getItems().clear();

        for (String name : grouped.keySet()) {
            MenuItem item = new MenuItem(name);
            item.setOnAction(e -> {
                searchField.setText(name);
                suggestionMenu.hide();
                showMultipleStopsOnMap(grouped.get(name));
            });
            suggestionMenu.getItems().add(item);
        }

        if (!suggestionMenu.isShowing()) {
            suggestionMenu.show(searchField, javafx.geometry.Side.BOTTOM, 0, 0);
        }
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;

        new Thread(() -> {
            List<StopDTO> stops = stopService.getStopsByName(query);
            Platform.runLater(() -> {
                clearAndShowStops(stops);
                if (stops.isEmpty()) showAlert("Nem található megálló: " + query);
            });
        }).start();
    }

    private void clearAndShowStops(List<StopDTO> stops) {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.call("clearStops");
        for (StopDTO stop : stops) {
            window.call("addStopTracked", stop.getLat(), stop.getLon(), stop.getName());
        }
    }

    private void showMultipleStopsOnMap(List<StopDTO> stops) {
        if (stops == null || stops.isEmpty()) return;

        clearAndShowStops(stops);
        StopDTO first = stops.get(0);
        webEngine.executeScript(String.format("focusOn(%f, %f)", first.getLat(), first.getLon()));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Információ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void javaLog(String message) {
        System.out.println("Received from JS: " + message);
    }

    public void javaGetStopDetails(String name, double lat, double lon) {
        new Thread(() -> {
            try {
                String stopId = stopService.getStopIdByName(name);
                List<DepartureDTO> departures = new DepartureService(new DefaultApi(ApiClientProvider.getClient()))
                        .getDepartures(stopId);
                String popupHtml = DepartureViewBuilder.build(departures);

                Platform.runLater(() -> {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.call("updatePopupContent", name, popupHtml);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
private void fetchSuggestions(String query) {
    new Thread(() -> {
        List<StopDTO> allStops = stopService.getStopsByName(query);
        Map<String, List<StopDTO>> grouped = allStops.stream()
                .collect(Collectors.groupingBy(StopDTO::getName));

        Platform.runLater(() -> {
            if (allStops.isEmpty()) {
                suggestionMenu.hide();
            } else {
                suggestionMenu.getItems().clear();
                for (String name : grouped.keySet()) {
                    MenuItem item = new MenuItem(name);
                    item.setOnAction(e -> {
                        searchField.setText(name);
                        suggestionMenu.hide();
                        showMultipleStopsOnMap(grouped.get(name));
                    });
                    suggestionMenu.getItems().add(item);
                }

                if (!suggestionMenu.isShowing()) {
                    suggestionMenu.show(searchField, javafx.geometry.Side.BOTTOM, 0, 0);
                }
            }
        });
    }).start();
}

}
