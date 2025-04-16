package futar.futar.controller;
import futar.futar.controller.*;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;

public class MainViewController {

    @FXML private WebView mapView;
    @FXML private TextField searchField;
    @FXML private TextField departureField;
    @FXML private TextField arrivalField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ComboBox<String> timeModeBox;
    @FXML private VBox routePlannerPanel;

    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

    private MapController mapController;
    private SearchController searchController;
    private RoutePlannerController routePlannerController;

    @FXML
    public void initialize() {
        this.mapController = new MapController(mapView);

        // 💥 Add this: átadjuk magunkat a JavaScriptnek
        mapController.setJavaConnector(this);

        this.searchController = new SearchController(searchField, debounce, mapController);
        this.routePlannerController = new RoutePlannerController(
                departureField, arrivalField, datePicker, timeField, timeModeBox
        );

        searchController.setupSearchField();
        routePlannerController.setupSuggestionHandlers();
        routePlannerController.setDefaultDateTime();
    }


    @FXML
    private void onSwapStops() {
        routePlannerController.swapStops();
    }

    @FXML
    private void onSearch() {
        searchController.performSearch();
    }

    @FXML
    private void onPlanRoute() {
        routePlannerController.planRoute();
    }

    @FXML
    private void onDepartureKeyTyped() {
        routePlannerController.handleDepartureSuggestions();
    }

    @FXML
    private void onArrivalKeyTyped() {
        routePlannerController.handleArrivalSuggestions();
    }

    @FXML
    public void onToggleRoutePlanner(ActionEvent actionEvent) {
        boolean visible = routePlannerPanel.isVisible();
        routePlannerPanel.setVisible(!visible);
        routePlannerPanel.setManaged(!visible);
    }

    public void javaLog(String message) {
        mapController.logFromJavaScript(message);
    }
    @FXML public void onIncreaseHour() { routePlannerController.onIncreaseHour(); }
    @FXML public void onDecreaseHour() { routePlannerController.onDecreaseHour(); }
    @FXML public void onIncreaseMinute() { routePlannerController.onIncreaseMinute(); }
    @FXML public void onDecreaseMinute() { routePlannerController.onDecreaseMinute(); }
    @FXML public void onSetNow() { routePlannerController.onSetNow(); }

    public void javaGetStopDetails(String stopId, String name, double lat, double lon) {
        mapController.handleStopDetails(stopId, name, lat, lon);
    }
}