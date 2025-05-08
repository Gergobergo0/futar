package futar.futar.view;

import futar.futar.controller.map.MapController;
import futar.futar.controller.map.PopupManager;
import futar.futar.model.FavoriteRoute;
import futar.futar.model.PathStep;
import futar.futar.service.FavoriteManager;
import futar.futar.service.StopService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RoutePlanBuilder {

    public static void show(List<PathStep> steps, Object unused, PopupManager popupManager) {
        if (steps == null || steps.isEmpty()) return;
        steps = mergeSteps(steps);

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.NONE);
        popupStage.setTitle("Megtervezett útvonal");

        VBox routeLayout = new VBox(10);
        routeLayout.setPadding(new Insets(15));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (int i = 0; i < steps.size(); i++) {
            PathStep step = steps.get(i);

            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setSpacing(3);
            card.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc;");
            card.setStyle(card.getStyle() + "-fx-background-color: " + switch (step.getMode().toUpperCase()) {
                case "BUS" -> "#e3f2fd";
                case "TRAM" -> "#fff3e0";
                case "SUBWAY" -> "#ede7f6";
                case "WALK" -> "#e0f2f1";
                case "RAIL" -> "#ccbac7";
                default -> "#eeeeee";
            });

            String icon = switch (step.getMode().toUpperCase()) {
                case "BUS" -> "🚌 ";
                case "TRAM" -> "🚋 ";
                case "SUBWAY" -> "🚇 ";
                case "WALK" -> "🚶 ";
                default -> "➡️ ";
            };

            String labelText = step.getMode().equalsIgnoreCase("WALK")
                    ? "Séta"
                    : formatModeAndRoute(step.getMode(), step.getLabel());

            Node headerContent = step.getMode().equalsIgnoreCase("WALK")
                    ? new Label(icon + labelText)
                    : createTripLink(icon + labelText, step.getTripId());

            if (headerContent instanceof Labeled labeled) {
                labeled.setFont(Font.font("Arial", 16));
                labeled.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
            }


            headerContent.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
            ((Labeled) headerContent).setFont(Font.font("Arial", 16));

            HBox headerBox = new HBox(headerContent);
            headerBox.setPadding(new Insets(6));
            headerBox.setStyle("-fx-background-color: " + switch (step.getMode().toUpperCase()) {
                case "BUS" -> "#449ddd";
                case "TRAM" -> "#f9d949";
                case "SUBWAY" -> "#639f41";
                case "WALK" -> "#b2dfdb";
                case "RAIL" -> "#7f2d69";
                default -> "#eeeeee";
            } + "; -fx-background-radius: 8;");

            HBox fromTo = new HBox(5);

            if (step.getMode().equalsIgnoreCase("WALK")) {
                if (i == 0) {
                    fromTo.getChildren().add(new Label("Pozíciód → " + step.getTo()));
                } else {
                    fromTo.getChildren().addAll(
                            createStopLink(step.getFrom(), popupManager),
                            new Label(" → "),
                            createStopLink(step.getTo(), popupManager)
                    );
                }
            } else {
                fromTo.getChildren().addAll(
                        createStopLink(step.getFrom(), popupManager),
                        new Label(" → "),
                        createStopLink(step.getTo(), popupManager)
                );
            }

            fromTo.setStyle("-fx-font-size: 14;");
            Label time = new Label("Indulás: " + step.getDeparture() + " • Érkezés: " + step.getArrival());
            time.setStyle("-fx-text-fill: #555;");
            time.setFont(Font.font("Arial", 13));

            Label duration = new Label();
            try {
                LocalTime start = LocalTime.parse(step.getDeparture(), formatter);
                LocalTime end = LocalTime.parse(step.getArrival(), formatter);
                long minutes = Duration.between(start, end).toMinutes();
                duration.setText((step.getMode().equalsIgnoreCase("WALK") ? minutes == 0 ? "" : "Séta idő: " : "Utazási idő: ") + minutes + " perc");
                duration.setFont(Font.font("Arial", 13));
            } catch (Exception ignored) {}

            card.getChildren().addAll(headerBox, fromTo, time, duration);
            routeLayout.getChildren().add(card);

            if (i < steps.size() - 1) {
                Label sectionLabel = new Label("Következő szakasz");
                sectionLabel.setStyle("-fx-text-fill: gray;");
                sectionLabel.setFont(Font.font("Arial", 12));
                routeLayout.getChildren().add(sectionLabel);
            }
        }

        ScrollPane scrollPane = new ScrollPane(routeLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        final String finalFrom = steps.get(0).getFrom();
        final String finalTo = steps.get(steps.size() - 1).getTo();

        Button favoriteButton = new Button("Kedvenc útvonal");
        favoriteButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Útvonal elmentése");
            dialog.setHeaderText("Adj nevet a kedvenc útvonaladnak");
            dialog.setContentText("Név:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.isBlank()) {
                    FavoriteManager.getInstance().addRoute(new FavoriteRoute(name, finalFrom, finalTo));
                    new Alert(Alert.AlertType.INFORMATION, "Útvonal elmentve: " + name).showAndWait();
                }
            });
        });


        Button closeButton = new Button("Bezárás");
        closeButton.setOnAction(e -> popupStage.close());

        HBox buttons = new HBox(10, favoriteButton, closeButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VBox root = new VBox(10, scrollPane, buttons);
        root.setPadding(new Insets(10));

        popupStage.setScene(new Scene(root, 700, 520));
        popupStage.show();
    }

    private static Region createTripLink(String text, String tripId) {
        if (tripId == null || tripId.isBlank()) return new Label(text);
        Hyperlink link = new Hyperlink(text);
        link.setBorder(Border.EMPTY);
        link.setPadding(Insets.EMPTY);
        link.setOnAction(e -> {
            try {
                MapController.routeInfoDisplayer.displayRouteInfo(convertTripId(tripId));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return link;
    }

    private static Hyperlink createStopLink(String name, PopupManager popupManager) {
        Hyperlink link = new Hyperlink(name);
        link.setStyle("-fx-text-fill: #0066cc; -fx-underline: true;");
        link.setOnAction(evt -> {
            var stopDto = new StopService().getStopByName(name);
            if (stopDto != null) {
                popupManager.showFloatingPopupForStop(stopDto.getId(), stopDto.getName(), stopDto.getLat(), stopDto.getLon());
            }
        });
        return link;
    }

    private static String convertTripId(String rawTripId) {
        return (rawTripId != null && rawTripId.startsWith("BKK:")) ? "BKK_" + rawTripId.substring(4) : rawTripId;
    }

    private static String formatModeAndRoute(String mode, String label) {
        if (label == null) return mode;
        String[] parts = label.replace("[", "").replace("]", "").split(" ");
        if (parts.length < 2) return label;
        String route = parts[1].replace("BKK_", "");
        return switch (parts[0].toUpperCase()) {
            case "BUS" -> route + " busz";
            case "TRAM" -> route + " villamos";
            case "SUBWAY" -> route.replace("M", "") + " metró";
            case "RAIL" -> route + " vonat";
            default -> route;
        };
    }

    private static List<PathStep> mergeSteps(List<PathStep> steps) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
        List<PathStep> mergedSteps = new ArrayList<>();
        int i = 0;
        while (i < steps.size()) {
            PathStep step = steps.get(i);
            if (!step.getMode().equalsIgnoreCase("WALK")) {
                mergedSteps.add(step);
                i++;
                continue;
            }
            String from = step.getFrom();
            LocalTime departure = LocalTime.parse(step.getDeparture(), formatter);
            int walkStart = i;
            while (i < steps.size() && steps.get(i).getMode().equalsIgnoreCase("WALK")) i++;
            int walkEnd = i - 1;
            String to = steps.get(walkEnd).getTo();
            LocalTime arrival = LocalTime.parse(steps.get(walkEnd).getArrival(), formatter);
            PathStep merged = new PathStep();
            merged.setMode("WALK");
            merged.setFrom(from);
            merged.setTo(to);
            merged.setDeparture(departure.format(formatter));
            merged.setArrival(arrival.format(formatter));
            mergedSteps.add(merged);
        }
        return mergedSteps;
    }
}
