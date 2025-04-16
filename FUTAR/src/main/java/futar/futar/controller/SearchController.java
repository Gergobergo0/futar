package futar.futar.controller;

import futar.futar.model.StopDTO;
import futar.futar.service.StopService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;
import futar.futar.utils.UIUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchController {

    private TextField searchField;
    private PauseTransition debounce;
    private final StopService stopService = new StopService();
    private final ContextMenu suggestionMenu = new ContextMenu();
    private MapController mapController;


    public SearchController(TextField searchField, PauseTransition debounce, MapController mapController) {
        this.searchField = searchField;
        this.debounce = debounce;
        this.mapController = mapController;
    }

    public void setupSearchField() {
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
    public void initialize(TextField searchField, PauseTransition debounce, MapController mapController) {
        this.searchField = searchField;
        this.debounce = debounce;
        this.mapController = mapController;
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
                            mapController.clearAndShowStops(grouped.get(name));
                        });
                        suggestionMenu.getItems().add(item);
                    }

                    if (!suggestionMenu.isShowing()) {
                        suggestionMenu.show(searchField, Side.BOTTOM, 0, 0);
                    }
                }
            });
        }).start();
    }

    public void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;

        new Thread(() -> {
            List<StopDTO> stops = stopService.getStopsByName(query);
            Platform.runLater(() -> {
                mapController.showMultipleStopsOnMap(stops);
                if (stops.isEmpty()) UIUtils.showAlert("Nem található megálló: " + query);
            });
        }).start();
    }
}