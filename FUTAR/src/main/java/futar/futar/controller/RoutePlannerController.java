package futar.futar.controller;

import futar.futar.model.StopDTO;
import futar.futar.service.StopService;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.*;
import futar.futar.utils.UIUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoutePlannerController {

    private final TextField departureField;
    private final TextField arrivalField;
    private final DatePicker datePicker;
    private final TextField timeField;
    private final ComboBox<String> timeModeBox;
    private final ContextMenu departureSuggestionMenu = new ContextMenu();
    private final ContextMenu arrivalSuggestionMenu = new ContextMenu();
    private final StopService stopService = new StopService();

    public RoutePlannerController(TextField departureField, TextField arrivalField,
                                  DatePicker datePicker, TextField timeField,
                                  ComboBox<String> timeModeBox) {
        this.departureField = departureField;
        this.arrivalField = arrivalField;
        this.datePicker = datePicker;
        this.timeField = timeField;
        this.timeModeBox = timeModeBox;
    }

    public void setDefaultDateTime() {
        datePicker.setValue(LocalDate.now());
        LocalTime now = LocalTime.now();
        timeField.setText(now.format(DateTimeFormatter.ofPattern("HH:mm")));

        ContextMenu timeMenu = new ContextMenu();
        MenuItem nowItem = new MenuItem("Most");
        nowItem.setOnAction(e -> {
            LocalTime current = LocalTime.now();
            timeField.setText(current.format(DateTimeFormatter.ofPattern("HH:mm")));
        });
        timeMenu.getItems().add(nowItem);
        setupTimeFieldContextMenu();

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
        String timeText = timeField.getText().trim();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
        String mode = timeModeBox.getValue();

        if (departure.isEmpty() || arrival.isEmpty() || date == null || timeText.isEmpty() || mode == null) {
            UIUtils.showAlert("Kérlek, tölts ki minden mezőt az útvonaltervezéshez.");
            return;
        }

        System.out.println("Útvonaltervezés: " + departure + " → " + arrival + " @ " + date + " " + timeText + " (" + mode + ")");
    }

    public void setupSuggestionHandlers() {
        setupSuggestions(departureField, departureSuggestionMenu);
        setupSuggestions(arrivalField, arrivalSuggestionMenu);
    }

    public void handleDepartureSuggestions() {
        triggerSuggestion(departureField, departureSuggestionMenu);
    }

    public void handleArrivalSuggestions() {
        triggerSuggestion(arrivalField, arrivalSuggestionMenu);
    }

    private void setupSuggestions(TextField field, ContextMenu menu) {
        field.setOnKeyTyped(e -> triggerSuggestion(field, menu));
    }

    private void triggerSuggestion(TextField field, ContextMenu menu) {
        String text = field.getText().trim();
        if (text.length() < 2) {
            menu.hide();
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
                    });
                    menu.getItems().add(item);
                }
                if (!menu.isShowing()) {
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
        timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }




}