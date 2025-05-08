package futar.futar.controller;
import futar.futar.api.ApiClientProvider;
import futar.futar.controller.map.PopupManager;
import futar.futar.service.DepartureService;
import futar.futar.view.RoutePlanBuilder;
import futar.futar.model.StopDTO;
import futar.futar.service.StopService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.*;
import futar.futar.utils.UIUtils;
import futar.futar.model.TransitRoute;
import futar.futar.service.RoutePlannerService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.util.Duration;
import org.openapitools.client.api.DefaultApi;
/**
 * Az útvonaltervező vezérlőosztály, amely kezeli a megállók kiválasztását,
 * az idő és dátum beállításokat, és az útvonal kiszámítását.
 */

public class RoutePlannerController {
    // UI elemek és szolgáltatások

    private final Spinner<Integer> hourSpinner;
    private final Spinner<Integer> minuteSpinner;
    //private final RoutePlannerService routePlannerService = new RoutePlannerService();
    private final TextField departureField;
    private final TextField arrivalField;
    private final DatePicker datePicker;
    private final TextField timeField;
    private final ComboBox<String> timeModeBox;
    private final ContextMenu departureSuggestionMenu = new ContextMenu();
    private final ContextMenu arrivalSuggestionMenu = new ContextMenu();
    private final StopService stopService = new StopService();
    private final PopupManager popupManager;
    private final RoutePlannerService routePlannerService = new RoutePlannerService();

    // private final RoutePlannerService routePlannerService = new RoutePlannerService();
    //private final OtpRoutePlannerService otpRoutePlannerService = new OtpRoutePlannerService();
    public final DepartureService departureService;
    private Spinner<Integer> walkDistanceSpinner;
    //private final R5RoutePlannerService r5RoutePlannerService = new R5RoutePlannerService();
    private ComboBox<String> walkSpeedBox;

    /**
     * Konstruktor, beállítja az összes kapcsolódó mezőt és a keresés debounce-t.
     */

    public RoutePlannerController(TextField departureField, TextField arrivalField,
                                  DatePicker datePicker,
                                  Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner, TextField timeField,
                                  ComboBox<String> timeModeBox,
                                  PopupManager popupManager) {
        this.departureField = departureField;
        this.arrivalField = arrivalField;
        this.datePicker = datePicker;
        this.hourSpinner = hourSpinner;
        this.minuteSpinner = minuteSpinner;
        this.timeField = timeField;
        this.timeModeBox = timeModeBox;
        this.popupManager = popupManager;
        this.departureService = new DepartureService();
        setupDebouncedSearch(departureField, fromDebounce, this::handleDepartureSuggestionsDebounced);
        setupDebouncedSearch(arrivalField, toDebounce, this::handleArrivalSuggestionsDebounced);


        setupFocusListeners();

    }

    /**
     * Beállítja a gyaloglási távolság és sebesség mezőket.
     */

    public void setWalkControls(Spinner<Integer> walkSpinner, ComboBox<String> speedBox) {
        this.walkDistanceSpinner = walkSpinner;
        this.walkSpeedBox = speedBox;

        // Alapértékek beállítása, ha szükséges
        walkSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 5000, 1500, 100));
        speedBox.setValue("ÁTLAGOS");
    }
    /**
     * Javaslatok feldolgozása a kiinduló megállóhoz.
     */

    private void handleDepartureSuggestionsDebounced(String query) {
        triggerSuggestionWithQuery(departureField, departureSuggestionMenu, query);
    }
    /**
     * Javaslatok feldolgozása a cél megállóhoz.
     */

    private void handleArrivalSuggestionsDebounced(String query) {
        triggerSuggestionWithQuery(arrivalField, arrivalSuggestionMenu, query);
    }

    /**
     * Beállítja az alapértelmezett dátumot és időt a jelenlegi időpontra.
     */

    public void setDefaultDateTime() {
        datePicker.setValue(LocalDate.now());

        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));

        LocalTime now = LocalTime.now();
        hourSpinner.getValueFactory().setValue(now.getHour());
        minuteSpinner.getValueFactory().setValue(now.getMinute());
        timeModeBox.setValue("Indulás");
    }
    /**
     * Felcseréli a kiinduló és cél megállót.
     */

    public void swapStops() {
        String from = departureField.getText();
        String to = arrivalField.getText();
        departureField.setText(to);
        arrivalField.setText(from);
    }
    /**
     * Megtervezi az útvonalat a megadott beállítások alapján.
     */

    public void planRoute() {
        String departure = departureField.getText().trim();
        String arrival = arrivalField.getText().trim();
        String timeText = String.format("%02d:%02d", hourSpinner.getValue(), minuteSpinner.getValue());
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
        String mode = timeModeBox.getValue();
        int maxWalkDistance = walkDistanceSpinner.getValue();
        double walkSpeed;

        switch (walkSpeedBox.getValue()) {
            case "LASSÚ" -> walkSpeed = 0.8;
            case "GYORS" -> walkSpeed = 1.5;
            case "FUTÁS" -> walkSpeed = 2.0;
            default -> walkSpeed = 1.2; // ÁTLAGOS
        }

        if (departure.isEmpty() || arrival.isEmpty() || date == null || timeText.isEmpty() || mode == null) {
            UIUtils.showAlert("Kérlek, tölts ki minden mezőt az útvonaltervezéshez.");
            return;
        }

        System.out.println("Útvonaltervezés (OTP): " + departure + " → " + arrival + " @ " + date + " " + timeText + " (" + mode + ")");
        String transportMode = "TRANSIT,WALK";

        StopDTO fromStop = stopService.getStopByName(departure);
        StopDTO toStop = stopService.getStopByName(arrival);
        String fromCoords = fromStop.getLat() + "," + fromStop.getLon();
        String toCoords = toStop.getLat() + "," + toStop.getLon();





        if (fromCoords == null || toCoords == null) {
            UIUtils.showAlert("Ismeretlen helyszín: „" + departure + "” vagy „" + arrival + "”.");
            return;
        }

        new Thread(() -> {
            try {
                boolean arriveBy = mode.equals("Érkezés");

                RoutePlannerService planner = new RoutePlannerService();
                TransitRoute route = routePlannerService.planRoute(
                        departure,
                        fromStop.getLat(), fromStop.getLon(),
                        arrival,
                        toStop.getLat(), toStop.getLon(),
                        timeText,
                        date,
                        transportMode,
                        arriveBy
                );


                Platform.runLater(() -> {
                    if (route != null && !route.getSteps().isEmpty()) {
                        RoutePlanBuilder.show(route.getSteps(), departureService, popupManager);
                    } else {
                        UIUtils.showAlert("Nincs találat a keresett útvonalra.");
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> UIUtils.showAlert("Hiba történt az útvonaltervezés során:\n" + e.getMessage()));
            }
        }).start();


    }

    /**
     * Egy kedvenc útvonal megadása alapján automatikusan kitölti a mezőket és elindítja a tervezést.
     */

    public void planFavoriteRoute(String from, String to) {
        departureField.setText(from);
        arrivalField.setText(to);
        setDefaultDateTime();  // beállítjuk a dátumot és időt most-ra
        planRoute(); // ez meghívja a meglévő tervező logikát
    }


    /**
     * Javaslatok megjelenítése egy adott mezőhöz.
     */

    private void triggerSuggestionWithQuery(TextField field, ContextMenu menu, String text) {
        if (text.length() < 2) {
            Platform.runLater(menu::hide);
            return;
        }

        new Thread(() -> {
            List<StopDTO> stops = stopService.getStopsByName(text);
            Map<String, List<StopDTO>> grouped = stops.stream()
                    .collect(Collectors.groupingBy(StopDTO::getName));

            Platform.runLater(() -> {
                menu.getItems().clear();
                for (String name : grouped.keySet()) {
                    MenuItem item = new MenuItem(name);
                    item.setOnAction(e -> {
                        field.setText(name);
                        menu.hide();
                        field.getParent().requestFocus();
                        popupManager.tryShowRoutePreview(departureField.getText(), arrivalField.getText());
                    });
                    menu.getItems().add(item);
                }
                if (!menu.isShowing() && !grouped.isEmpty()) {
                    menu.show(field, Side.BOTTOM, 0, 0);
                }
            });
        }).start();
    }




    /**
     * Idő beállításának finomhangolása adott eltéréssel.
     */

    private void adjustTime(int hourDelta, int minuteDelta) {
        try {
            LocalTime time = LocalTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            time = time.plusHours(hourDelta).plusMinutes(minuteDelta);
            timeField.setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        } catch (Exception e) {
            timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    public void onIncreaseHour() {
        adjustTime(1, 0);
    }

    public void onDecreaseHour() {
        adjustTime(-1, 0);
    }

    public void onIncreaseMinute() {
        adjustTime(0, 1);
    }

    public void onDecreaseMinute() {
        adjustTime(0, -1);
    }
    /**
     * A jelenlegi idő beállítása a Spinner mezőkbe.
     */

    public void onSetNow() {
        LocalTime now = LocalTime.now();
        hourSpinner.getValueFactory().setValue(now.getHour());
        minuteSpinner.getValueFactory().setValue(now.getMinute());
    }
    /**
     * Automatikus előnézet frissítése, ha fókusz elvész.
     */

    private void setupFocusListeners() {
        departureField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                popupManager.tryShowRoutePreview(departureField.getText(), arrivalField.getText());
            }
        });
        arrivalField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                popupManager.tryShowRoutePreview(departureField.getText(), arrivalField.getText());
            }
        });
    }

    private final PauseTransition fromDebounce = new PauseTransition(Duration.millis(500));
    private final PauseTransition toDebounce = new PauseTransition(Duration.millis(500));
    /**
     * Debounce alapú keresőmező beállítás.
     */

    public void setupDebouncedSearch(TextField field, PauseTransition debounce, Consumer<String> onSearch) {
        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() < 2) {
                debounce.stop();
                return;
            }

            debounce.setOnFinished(e -> onSearch.accept(newText));
            debounce.playFromStart();
        });
    }




}