package futar.futar.view;

import futar.futar.controller.map.PopupManager;
import futar.futar.model.FavoriteRoute;
import futar.futar.model.PathStep;
import futar.futar.service.DepartureService;
import futar.futar.service.FavoriteManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import futar.futar.controller.map.MapController;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoutePlanBuilder {

    public static void show(List<PathStep> steps, DepartureService departureService, PopupManager popupManager) {
        if (steps == null || steps.isEmpty()) return;
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.NONE);
        popupStage.setTitle("Megtervezett útvonal");

        VBox routeLayout = new VBox(10);
        routeLayout.setPadding(new Insets(15));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (int i = 0; i < steps.size(); i++) {
            PathStep step = steps.get(i);
            String headsignText = "";

            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setSpacing(3);
            card.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc;");

            String color = switch (step.getMode().toUpperCase()) {
                case "BUS" -> "#e3f2fd";
                case "TRAM" -> "#fff3e0";
                case "SUBWAY" -> "#ede7f6";
                case "WALK" -> "#e0f2f1";
                case "RAIL" -> "#ccbac7";
                default -> "#eeeeee";
            };
            card.setStyle(card.getStyle() + "-fx-background-color: " + color + ";");

            String icon = switch (step.getMode().toUpperCase()) {
                case "BUS" -> "🚌 ";
                case "TRAM" -> "🚋 ";
                case "SUBWAY" -> "🚇 ";
                case "WALK" -> "🚶 ";
                default -> "➡️ ";
            };

            String labelText;
            if (step.getMode().equalsIgnoreCase("WALK")) {
                labelText = "Séta";
            } else {
                labelText = formatModeAndRoute(step.getMode(), step.getLabel());
            }

            Region headerContent;
            if (step.getMode().equalsIgnoreCase("WALK")) {
                Label plainLabel = new Label(icon + labelText);
                plainLabel.setFont(Font.font("Arial", 16));
                plainLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                headerContent = plainLabel;
            } else {
                Hyperlink headerLink = new Hyperlink(icon + labelText);
                headerLink.setFont(Font.font("Arial", 16));
                headerLink.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                headerLink.setWrapText(true);
                headerLink.setBorder(Border.EMPTY);
                headerLink.setPadding(Insets.EMPTY);

                if (step.getTripId() != null && !step.getTripId().isBlank()) {
                    final String convertedTripId = convertTripId(step.getTripId());
                    headerLink.setOnAction(event -> {
                        System.out.println("🔍 Kattintás történt headsign-re: " + convertedTripId);
                        try {
                            MapController.routeInfoDisplayer.displayRouteInfo(convertedTripId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                headerContent = headerLink;
            }


            String headerColor = switch (step.getMode().toUpperCase()) {
                case "BUS" -> "#449ddd";
                case "TRAM" -> "#f9d949";
                case "SUBWAY" -> "#639f41";
                case "WALK" -> "#b2dfdb";
                case "RAIL" -> "#7f2d69";
                default -> "#eeeeee";
            };

            HBox headerBox = new HBox(headerContent);
            headerBox.setPadding(new Insets(6));
            headerBox.setStyle("-fx-background-color: " + headerColor + "; -fx-background-radius: 8;");

            Label fromTo = new Label(step.getFrom() + " → " + step.getTo());
            fromTo.setFont(Font.font("Arial", 14));

            Label time = new Label("Indulás: " + step.getDeparture() + " • Érkezés: " + step.getArrival());
            time.setStyle("-fx-text-fill: #555;");
            time.setFont(Font.font("Arial", 13));

            card.getChildren().addAll(headerBox, fromTo, time);

            try {
                LocalTime start = LocalTime.parse(step.getDeparture(), formatter);
                LocalTime end = LocalTime.parse(step.getArrival(), formatter);
                long minutes = Duration.between(start, end).toMinutes();
                Label duration = new Label("⏱️ Utazási idő: " + minutes + " perc");
                duration.setFont(Font.font("Arial", 13));
                card.getChildren().add(duration);
            } catch (Exception ignored) {}

            routeLayout.getChildren().add(card);

            if (i < steps.size() - 1) {
                Label arrow = new Label("⬇️ Következő szakasz");
                arrow.setStyle("-fx-text-fill: gray;");
                arrow.setFont(Font.font("Arial", 12));
                routeLayout.getChildren().add(arrow);
            }
        }

        ScrollPane scrollPane = new ScrollPane(routeLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        Button favoriteButton = new Button("⭐ Kedvenc útvonal");
        favoriteButton.setOnAction(e -> {
            String from = steps.get(0).getFrom();
            String to = steps.get(steps.size() - 1).getTo();

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("⭐ Útvonal elmentése");
            dialog.setHeaderText("Adj nevet a kedvenc útvonaladnak");
            dialog.setContentText("Név:");

            dialog.showAndWait().ifPresent(name -> {
                if (!name.isBlank()) {
                    FavoriteRoute route = new FavoriteRoute(name, from, to);
                    FavoriteManager.getInstance().addRoute(route);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Siker");
                    alert.setHeaderText(null);
                    alert.setContentText("✅ Útvonal elmentve: " + name);
                    alert.showAndWait();
                }
            });
        });

        Button closeButton = new Button("Bezárás");
        closeButton.setOnAction(e -> popupStage.close());

        HBox buttons = new HBox(10, favoriteButton, closeButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10, scrollPane, buttons);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 700, 520);
        popupStage.setScene(scene);
        popupStage.show();
    }

    public static String convertTripId(String rawTripId) {
        if (rawTripId == null) return null;
        if (rawTripId.startsWith("BKK:")) {
            return "BKK_" + rawTripId.substring(4);
        }
        return rawTripId;
    }

    private static String formatModeAndRoute(String mode, String label) {
        if (label == null) return mode;
        String cleaned = label.replace("[", "").replace("]", "");
        String[] parts = cleaned.split(" ");
        if (parts.length < 2) return label;

        String type = parts[0];
        String route = parts[1];

        if (route.startsWith("BKK_")) {
            route = route.substring(4);
        }

        return switch (type.toUpperCase()) {
            case "BUS" -> route + " busz";
            case "TRAM" -> route + " villamos";
            case "SUBWAY" -> route.replace("M", "") + " metró";
            case "RAIL" -> route + " vonat";
            default -> route;
        };
    }
}
