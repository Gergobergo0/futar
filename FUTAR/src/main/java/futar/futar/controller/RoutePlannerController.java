package futar.futar.controller;
import futar.futar.controller.map.PopupManager;
import futar.futar.controller.map.RouteInfoDisplayer;
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
/**
 * Ez az osztály felelős az UI és útvonaltervezési logika összekötéséért.
 * Kezeli a kiindulási, célállomási beviteli mezőket, dátum, idő bellításokat, az útvonal kiszámítását.
 * Automatikus javaslatokat nyújt a megállónevek kereséséhez, megjeleníti az útvonal előnézetet
 */

public class RoutePlannerController {


    private final Spinner<Integer> hourSpinner;
    private final Spinner<Integer> minuteSpinner;
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
    public final DepartureService departureService;
    private Spinner<Integer> walkDistanceSpinner;
    private ComboBox<String> walkSpeedBox;

    /**
     * belső osztály az útvonal paraméterek egységesítése érdekében
     */
    private static final class RouteParameters {
        String departure;
        String arrival;
        String date;
        String time;
        String mode;
        int maxWalkDistance;
        double walkSpeed;
    }


    /**
     * Létrehozza a {@code RoutePlannerController} osztályt.
     * @param departureField    indulási állomás beviteli mező
     * @param arrivalField      érkezési állomás bevitel mező
     * @param datePicker        dátumválasztó komponens
     * @param hourSpinner       órát állító spinner
     * @param minuteSpinner     percet állító spinner
     * @param timeField         szöveges időmező (óra:perc)
     * @param timeModeBox       mező az indulás/érkezés időhöz
     * @param popupManager      javaslatok, előnézetekhez szükséges komponens
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
        setupSuggestionHandlers();
        setupFocusListeners();

    }


    /**
     * (!NINCS MÖGÖTTE LOGIKA!)Beállítja a gyaloglás maximális távolságát és sebességét
     * @param walkSpinner   Gyaloglás távolságát beállító spinner
     * @param speedBox      //gyaloglás gyorsasásgát beállító legördülő menü
     */
    public void setWalkControls(Spinner<Integer> walkSpinner, ComboBox<String> speedBox) {
        this.walkDistanceSpinner = walkSpinner;
        this.walkSpeedBox = speedBox;

        // Alapértékek beállítása, ha szükséges
        walkSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 5000, 1500, 100));
        speedBox.setValue("ÁTLAGOS");
    }

    /**
     * beállítja az automatikus javaslatkezelést a cél és indulási állomásokhoz
     */
    private void setupSuggestionHandlers() {
        setupDebouncedSearch(departureField, fromDebounce, q -> triggerSuggestion(departureField, departureSuggestionMenu, q));
        setupDebouncedSearch(arrivalField, toDebounce, q -> triggerSuggestion(arrivalField, arrivalSuggestionMenu, q));
    }


    /**
     * Aktuális dátum és időt beállítása az ui-n
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
     * felcseréli a kiválasztott indulási/érkezési megállókat
     */
    public void swapStops() {
        String from = departureField.getText();
        String to = arrivalField.getText();
        departureField.setText(to);
        arrivalField.setText(from);
    }

    /**
     * Elindítja az útvonaltervezést az UI-ban kitöltött információk alapján
     */
    public void planRoute() {
        RouteParameters params = gatherRouteParameters();
        if (params == null) return;

        StopDTO[] stops = resolveStops(params.departure, params.arrival);
        if (stops == null) return;

        System.out.println("[JAVA] Útvonaltervezés: " + params.departure + " -> " + params.arrival +
                " @ " + params.date + " " + params.time + " (" + params.mode + ")");

        runRoutePlanning(params, stops[0], stops[1]);
    }


    /**
     * Egy mentett útvonal alapján kitölti a mezőket, elindítja az útvonaltervezést
     * @param from kiindulősi megálló
     * @param to érkezési megálló
     */
    public void planFavoriteRoute(String from, String to) {
        departureField.setText(from);
        arrivalField.setText(to);
        setDefaultDateTime();  // beállítjuk a dátumot és időt most-ra
        planRoute(); // ez meghívja a meglévő tervező logikát
    }


    /**
     */
    /**
     *      * Előkészíti a a keresési javaslatokat a megállónevekhez adott szöveg alapján
     * @param field szövegmező ahova a felhasználü gépel
     * @param menu  megjelenítendő javaslatmenü
     * @param text  keresési szöveg
     */
    private void triggerSuggestion(TextField field, ContextMenu menu, String text) {
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
                    Platform.runLater(() -> {
                        if (field.getScene() != null) {
                            menu.show(field, Side.BOTTOM, 0, 0);
                        }
                    });
                }
            });
        }).start();
    }


    /**
     * localTime-ra alakítja a a szövegmezőt HH:mm formátumról, ha pl (1, -15) jelenik meg 1 órával előre és 15 percre visszaállítja az időt
     * @param hourDelta óra eltérési érték
     * @param minuteDelta perc eltérési érték
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

    /**
     * növeli az óra értékét a menüben
     */
    public void onIncreaseHour() {
        adjustTime(1, 0);
    }

    /**
     * csökkenti az óra értékét a menüben
     */
    public void onDecreaseHour() {
        adjustTime(-1, 0);
    }

    /**
     * növeli a perc értékét a menünen
     */
    public void onIncreaseMinute() {
        adjustTime(0, 1);
    }

    /**
     * csökkenti a perc értékét a menüben
     */
    public void onDecreaseMinute() {
        adjustTime(0, -1);
    }

    /**
     * jelenlegi időt állítja be a menüben
     */
    public void onSetNow() {
        LocalTime now = LocalTime.now();
        hourSpinner.getValueFactory().setValue(now.getHour());
        minuteSpinner.getValueFactory().setValue(now.getMinute());
    }

    /**
     * beállítja az automatikus javaslatkeresést a kiindulási és célállomásra
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

    /**
     * indulási mező keresési késlteltése
     */
    private final PauseTransition fromDebounce = new PauseTransition(Duration.millis(500));
    /**
     * érkezési mező keresési késleltetése
     */
    private final PauseTransition toDebounce = new PauseTransition(Duration.millis(500));

    /**
     * késlelteti a keresést, hogy ne minden billentyűleütés után indítson ajánlási keresést, hanem csak ha a felhasználó rövid idő után nem gépel tovább
     * @param field     megfigyelt menző
     * @param debounce  PauseTransition időzítő
     * @param onSearch  callback függvény , lefut ha kereshet
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

    /**
     * (!HIÁNYOS!) összegyűjti az útvonaltervezéshez szükséges paraméterket a guiról
     * @return {@code RouteParameters} objektum, vagy {@code null}, ha sikertelen
     */
    private RouteParameters gatherRouteParameters() {
        RouteParameters params = new RouteParameters();
        params.departure = departureField.getText().trim();
        params.arrival = arrivalField.getText().trim();
        params.date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
        params.time = String.format("%02d:%02d", hourSpinner.getValue(), minuteSpinner.getValue());
        params.mode = timeModeBox.getValue();
        params.maxWalkDistance = walkDistanceSpinner.getValue();

        switch (walkSpeedBox.getValue()) {
            case "LASSÚ" -> params.walkSpeed = 0.8;
            case "GYORS" -> params.walkSpeed = 1.5;
            case "FUTÁS" -> params.walkSpeed = 2.0;
            default -> params.walkSpeed = 1.2;
        }

        if (params.departure.isEmpty() || params.arrival.isEmpty() ||
                params.date == null || params.time.isEmpty() || params.mode == null) {
            UIUtils.showAlert("Kérlek, tölts ki minden mezőt az útvonaltervezéshez.");
            return null;
        }

        return params;
    }

    /**
     * lekéri a megadott megállónevekhez tartozó StopDTO objektimokat
     * @param departure indulási név
     * @param arrival   érkezési név
     * @return ha mindkét megállót megtalálja visszaadja a két stopDTO-t
     */
    private StopDTO[] resolveStops(String departure, String arrival) {
        StopDTO fromStop = stopService.getStopByName(departure);
        StopDTO toStop = stopService.getStopByName(arrival);
        if (fromStop == null || toStop == null) {
            UIUtils.showAlert("Ismeretlen helyszín: „" + departure + "” vagy „" + arrival + "”.");
            return null;
        }
        return new StopDTO[]{fromStop, toStop};
    }

    /**
     *  Külön szálon elindítja az útvonaltervezést, majd a visszatérést feldolgozza
     * @param params    megadott paraméterek
     * @param fromStop  kiindulási megálló neve
     * @param toStop    érkezési megálló neve
     */
    private void runRoutePlanning(RouteParameters params, StopDTO fromStop, StopDTO toStop) {
        new Thread(() -> {
            try {
                boolean arriveBy = params.mode.equals("Érkezés");
                TransitRoute route = routePlannerService.planRoute(
                        params.departure,
                        fromStop.getLat(), fromStop.getLon(),
                        params.arrival,
                        toStop.getLat(), toStop.getLon(),
                        params.time,
                        params.date,
                        "TRANSIT,WALK",
                        arriveBy
                );

                Platform.runLater(() -> displayRoute(route));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> UIUtils.showAlert("Hiba történt az útvonaltervezés során."));
                System.out.println("[HIBA - runRoutePlanning]: " + e.getMessage());
            }
        }).start();
    }

    /**
     * útvonaltervezési ablak megjelenítése
     * @param route útvonal
     */
    private void displayRoute(TransitRoute route) {
        if (route != null && !route.getSteps().isEmpty()) {
            RoutePlanBuilder.show(route.getSteps(), popupManager, new RouteInfoDisplayer(popupManager));
        } else {
            UIUtils.showAlert("Nincs találat a keresett útvonalra.");
        }
    }
}