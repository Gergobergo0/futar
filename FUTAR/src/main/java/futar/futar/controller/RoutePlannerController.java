package futar.futar.controller;

import futar.futar.controller.map.PopupManager;
import futar.futar.model.StopDTO;
import futar.futar.service.StopService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.*;
import futar.futar.utils.UIUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import futar.futar.service.RoutePlannerService;
import javafx.util.Duration;

public class RoutePlannerController {
    private final Spinner<Integer> hourSpinner;
    private final Spinner<Integer> minuteSpinner;
    private final RoutePlannerService routePlannerService = new RoutePlannerService();
    private final TextField departureField;
    private final TextField arrivalField;
    private final DatePicker datePicker;
    private final TextField timeField;
    private final ComboBox<String> timeModeBox;
    private final ContextMenu departureSuggestionMenu = new ContextMenu();
    private final ContextMenu arrivalSuggestionMenu = new ContextMenu();
    private final StopService stopService = new StopService();
    private final PopupManager popupManager;

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
        setupDebouncedSearch(departureField, fromDebounce, this::handleDepartureSuggestionsDebounced);
        setupDebouncedSearch(arrivalField, toDebounce, this::handleArrivalSuggestionsDebounced);

        setupFocusListeners();
    }

    private void handleDepartureSuggestionsDebounced(String query) {
        triggerSuggestionWithQuery(departureField, departureSuggestionMenu, query);
    }

    private void handleArrivalSuggestionsDebounced(String query) {
        triggerSuggestionWithQuery(arrivalField, arrivalSuggestionMenu, query);
    }


    public void setDefaultDateTime() {
        datePicker.setValue(LocalDate.now());

        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));

        LocalTime now = LocalTime.now();
        hourSpinner.getValueFactory().setValue(now.getHour());
        minuteSpinner.getValueFactory().setValue(now.getMinute());
        timeModeBox.setValue("Indulás");
    }

    public void swapStops() {
        String from = departureField.getText();
        String to = arrivalField.getText();
        departureField.setText(to);
        arrivalField.setText(from);
    }

    public void planRoute() {
        String departure = departureField.getText().trim();
        String arrival = arrivalField.getText().trim();
        String timeText = String.format("%02d:%02d", hourSpinner.getValue(), minuteSpinner.getValue());
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
        String mode = timeModeBox.getValue();

        if (departure.isEmpty() || arrival.isEmpty() || date == null || timeText.isEmpty() || mode == null) {
            UIUtils.showAlert("Kérlek, tölts ki minden mezőt az útvonaltervezéshez.");
            return;
        }

        System.out.println("Útvonaltervezés: " + departure + " → " + arrival + " @ " + date + " " + timeText + " (" + mode + ")");

        // 🔽 Itt hívjuk meg a választási lehetőségeket
        new Thread(() -> {
            List<RoutePlannerService.RoutePath> routes = routePlannerService.findTopDirectRoutes(departure, arrival, 3);

            Platform.runLater(() -> {
                System.out.println("\n--- TOP 3 ÚTVONAL a „" + departure + " → " + arrival + "” között ---");
                routePlannerService.printDirectRoutesPretty(routes);
            });
        }).start();

        routePlannerService.printNearbyStops("Újbuda-központ M");

    }



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



    private void setupTimeFieldContextMenu() {
        ContextMenu timeMenu = new ContextMenu();

        MenuItem incrementHour = new MenuItem("Óra +1");
        MenuItem decrementHour = new MenuItem("Óra -1");
        MenuItem incrementMinute = new MenuItem("Perc +1");
        MenuItem decrementMinute = new MenuItem("Perc -1");
        MenuItem setNow = new MenuItem("Most");

        incrementHour.setOnAction(e -> adjustTime(1, 0));
        decrementHour.setOnAction(e -> adjustTime(-1, 0));
        incrementMinute.setOnAction(e -> adjustTime(0, 1));
        decrementMinute.setOnAction(e -> adjustTime(0, -1));
        setNow.setOnAction(e -> timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));

        timeMenu.getItems().addAll(
                incrementHour, decrementHour,
                incrementMinute, decrementMinute,
                new SeparatorMenuItem(),
                setNow
        );

        timeField.setContextMenu(timeMenu);
    }

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

    public void onSetNow() {
        LocalTime now = LocalTime.now();
        hourSpinner.getValueFactory().setValue(now.getHour());
        minuteSpinner.getValueFactory().setValue(now.getMinute());
    }

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