package futar.futar.controller;

import futar.futar.controller.map.PopupManager;
import futar.futar.controller.map.StopMarkerDisplayer;
import futar.futar.model.StopDTO;
import futar.futar.service.StopService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import futar.futar.utils.UIUtils;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import futar.futar.controller.map.MapController;
import java.util.HashMap;
/**
 *A keresési mező kezeléséért felelős vezérlő, amely javaslatokat jelenít meg,
 * lekérdezi a megállókat, és megjeleníti azokat a térképen
 */
public class SearchController {
    /** A keresési javaslatok gyorsítótára (név alapján csoportosítva) */

    private final Map<String, List<StopDTO>> suggestionCache = new HashMap<>();
    private TextField searchField;
    private PauseTransition debounce;
    private final StopService stopService = new StopService();
    private final ContextMenu suggestionMenu = new ContextMenu();
    private final StopMarkerDisplayer stopMarkerDisplayer;
    private final PopupManager popupManager;
    /**
     * Konstruktor a kereső controller-hez
     *
     * @param searchField    a keresőmező (TextField)
     * @param debounce       késleltetett keresés (debounce) objektum
     * @param mapController  a térkép vezérlő, amelyből elérhetők a marker- és popupkezelők
     */
    public SearchController(TextField searchField, PauseTransition debounce, MapController mapController) {
        this.searchField = searchField;
        this.debounce = debounce;
        this.stopMarkerDisplayer = mapController.getStopMarkerDisplayer();
        this.popupManager = mapController.getPopupManager();
    }

    /**
     *Beállítja a keresőmező eseményfigyelőjét, amely a felhasználó gépelése alapján
     * debouncolva meghívja a javaslatok lekérdezését.
     */

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


    /**
     * Lekéri a megállók javaslatait a megadott keresés alapján,
     * és megjeleníti őket a javaslatmenüben.
     *
     * @param query a keresett kifejezés
     */
    private void fetchSuggestions(String query) {
        if (suggestionCache.containsKey(query)) {
            Platform.runLater(() -> updateSuggestionsMenu(suggestionCache.get(query)));
            return;
        }

        queryStops(query, stops -> {
            suggestionCache.put(query, stops);
            updateSuggestionsMenu(stops);
        });
    }


    /**
     * Frissíti a javaslatmenüt a megadott megállók alapján.
     *
     * @param stops a keresési eredmények
     */

    private void updateSuggestionsMenu(List<StopDTO> stops) {
        Map<String, List<StopDTO>> grouped = stops.stream()
                .collect(Collectors.groupingBy(StopDTO::getName));

        suggestionMenu.getItems().clear();

        for (String name : grouped.keySet()) {
            MenuItem item = new MenuItem(name);
            item.setOnAction(e -> {
                searchField.setText(name);
                suggestionMenu.hide();
                stopMarkerDisplayer.clearMap();
                popupManager.clearRoutePreview();
                List<StopDTO> selectedStops = grouped.get(name);
                if (selectedStops != null && !selectedStops.isEmpty()) {
                    stopMarkerDisplayer.showMultipleStops(selectedStops, false);
                }
            });
            suggestionMenu.getItems().add(item);
        }

        if (!grouped.isEmpty()) {
            suggestionMenu.show(searchField, Side.BOTTOM, 0, 0);
        } else {
            suggestionMenu.hide();
        }

    }

    /**
     * Keresés indítása gombnyomásra
     * Megjeleníti a megállókat a térképen vagy hibát, ha nincs találat.
     */
    public void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;

        queryStops(query, stops -> {
            stopMarkerDisplayer.clearMap();
            popupManager.clearRoutePreview();
            stopMarkerDisplayer.showMultipleStops(stops, false);
            if (stops.isEmpty()) UIUtils.showAlert("Nem található megálló: " + query);
        });
    }

    /**
     * háttérszálón elindítja a keresést egy kifejezés alapján
     * @param query     kersési kifejezés pl.: ast...
     * @param callback  lambda függvény amit egy lekérdezés után meghívjunk
     */
    private void queryStops(String query, Consumer<List<StopDTO>> callback) {
        new Thread(() -> {
            List<StopDTO> stops = stopService.getStopsByName(query);
            Platform.runLater(() -> callback.accept(stops));
        }).start();
    }

}
