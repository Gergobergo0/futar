package futar.futar.controller;

import futar.futar.controller.map.MapController;
import futar.futar.utils.JavaConnector;
import futar.futar.utils.NetworkUtils;
import futar.futar.view.FavoritesDialogBuilder;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import futar.futar.utils.UIUtils;
import javafx.event.ActionEvent;
/**
 * A fő nézet vezérlője (JavaFX FXML alapú GUI-hoz).
 * <p>
 * Kezeli a térkép megjelenítését, keresési mezőket, útvonaltervező panelt,
 * kedvencek dialógust, és más UI elemeket.
 */

public class MainViewController {
    // Térkép nézet

    @FXML private WebView mapView;
    // Keresési mezők

    @FXML private TextField searchField;
    @FXML private TextField departureField;
    @FXML private TextField arrivalField;
    // Dátum/idő beállítások

    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ComboBox<String> timeModeBox;
    @FXML private VBox routePlannerPanel;
    // Gyaloglás beállítások

    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML
    private Spinner<Integer> walkDistanceSpinner;
    @FXML
    public void onIncreaseHour() { routePlannerController.onIncreaseHour(); }
    @FXML public void onDecreaseHour() { routePlannerController.onDecreaseHour(); }
    @FXML public void onIncreaseMinute() { routePlannerController.onIncreaseMinute(); }
    @FXML public void onDecreaseMinute() { routePlannerController.onDecreaseMinute(); }
    @FXML public void onSetNow() { routePlannerController.onSetNow(); }
    @FXML
    private ComboBox<String> walkSpeedBox;
    /**
     * Megcseréli a kiindulási és célmegálló mezők értékeit.
     */

    @FXML public void onSwapStops() { routePlannerController.swapStops(); }
    /**
     * Elindítja a keresést a keresőmező tartalma alapján.
     */
    @FXML public void onSearch() { searchController.performSearch(); }
    /**
     * Elindítja az útvonaltervezést a megadott adatokkal.
     */
    @FXML public void onPlanRoute() { routePlannerController.planRoute(); }

    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));
    private MapController mapController;
    private SearchController searchController;
    private RoutePlannerController routePlannerController;

    /**
     * Inicializálja a GUI-t, hálózati kapcsolatot ellenőriz, betölti a térképet,
     * és beállítja az útvonaltervezőt és keresőt.
     */
    @FXML
    public void initialize() {
        if (!NetworkUtils.isApiReachable()) {
            UIUtils.showConnectionDialog();
        }
        this.mapController = new MapController(mapView);
        JavaConnector connector = new JavaConnector(mapController);
        mapController.setJavaConnector(connector);
        mapController.finishInit();
        this.searchController = new SearchController(searchField, debounce, mapController);
        this.routePlannerController = new RoutePlannerController(
                departureField, arrivalField,
                datePicker,
                hourSpinner, minuteSpinner,
                timeField,
                timeModeBox,
                mapController.getPopupManager()
        );

        searchController.setupSearchField();
        //routePlannerController.setupSuggestionHandlers();
        routePlannerController.setWalkControls(walkDistanceSpinner, walkSpeedBox);

        routePlannerController.setDefaultDateTime();

        mapController.getFavoriteManager().load();



    }
    /**
     * Megnyitja a kedvencek párbeszédablakot.
     */

    @FXML public void onShowFavorites() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Kedvencek");
        dialog.setHeaderText("Kedvenc megállók és útvonalak");

        VBox container = new VBox(10);

        FavoritesDialogBuilder.refreshContent(
                container,
                mapController.getFavoriteManager(),
                () -> {
                    // Frissítés esetén újra betöltjük az egész dialógust
                    Platform.runLater(this::onShowFavorites);
                },
                mapController.getPopupManager(),  // 💡 ezt eddig nem adtad át
                dialog , routePlannerController
        );

        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }



    /**
     * Megjeleníti vagy elrejti az útvonaltervező panelt.
     */

    @FXML
    public void onToggleRoutePlanner(ActionEvent event) {
        // Térképes panel láthatóságának váltása
        boolean visible = routePlannerPanel.isVisible();
        routePlannerPanel.setVisible(!visible);
        routePlannerPanel.setManaged(!visible);
    }
    @FXML private HBox advancedSettingsBox;
    /**
     * Megjeleníti vagy elrejti a haladó beállításokat (pl. gyaloglási távolság).
     */
    @FXML
    public void onToggleAdvancedSettings() {
        boolean show = !advancedSettingsBox.isVisible();
        advancedSettingsBox.setVisible(show);
        advancedSettingsBox.setManaged(show);
    }

    // Idő beállító gombok



    /*public void javaLog(String message) { mapController.logFromJavaScript(message);
       // System.out.println("JS:" + message);}*/

    /**
     * Hozzáadja az aktuálisan kiválasztott megállót a kedvencekhez.
     */
    public void addFavoriteStop() { mapController.addFavoriteStop(); }
    /**
     * Átváltja a kiválasztott megálló kedvenc státuszát.
     */
    public void toggleFavorite() { mapController.toggleFavorite(); }
    /**
     * Egy járatra kattintva annak részletes adatait jeleníti meg.
     *
     * @param tripId a járat azonosítója
     */

    public void handleRouteClick(String tripId) { mapController.handleRouteClick(tripId); }
    /**
     * A popup bezárásakor leállítja az automatikus frissítést.
     */

    public void onPopupClosed() {
        mapController.getPopupManager().stopAutoRefresh();
    }



}