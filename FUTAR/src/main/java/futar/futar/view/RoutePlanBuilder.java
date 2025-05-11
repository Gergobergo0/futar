package futar.futar.view;

import futar.futar.controller.map.PopupManager;
import futar.futar.controller.map.RouteInfoDisplayer;
import futar.futar.model.FavoriteRoute;
import futar.futar.model.PathStep;
import futar.futar.service.FavoriteManager;
import futar.futar.service.StopService;
import futar.futar.utils.Colors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ez az osztály felelős a megtervezett útvonal ablak megjelenítéséért
 * A megjelenített útvonal lépésekre bontva (PathStep) objektumok
 */
public class RoutePlanBuilder {
    /**
     * Megjeleníti az útvonal ablakot,
     * @param steps teljes útvonal lépéslistája
     * @param popupManager a megallokhoz tartozo popupok megjeleniteseert felelos komponens
     * @param routeInfoDisplayer az adott jaratok utvonal-informaciojanak megjeleniteseert felelos komponens
     */
    public static void show(List<PathStep> steps, PopupManager popupManager, RouteInfoDisplayer routeInfoDisplayer) {
        if (steps == null || steps.isEmpty()) return;
        steps = mergeSteps(steps);

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.NONE);
        popupStage.setTitle("Megtervezett útvonal");

        VBox routeLayout = new VBox(10);
        routeLayout.setPadding(new Insets(15));

        for (int i = 0; i < steps.size(); i++) {
            VBox card = createStepCard(steps.get(i), i, popupManager, routeInfoDisplayer);
            routeLayout.getChildren().add(card);
            if (i < steps.size() - 1) {
                routeLayout.getChildren().add(createSectionSeparator());
            }
        }

        ScrollPane scrollPane = createScrollableContent(routeLayout);

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

        Button pdfButton = new Button("Mentés PDF-be");
        pdfButton.setOnAction(e -> exportRouteAsPdf(routeLayout, popupStage));

        Button closeButton = new Button("Bezárás");
        closeButton.setOnAction(e -> popupStage.close());

        HBox buttons = new HBox(10, favoriteButton, pdfButton, closeButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VBox root = new VBox(10, scrollPane, buttons);
        root.setPadding(new Insets(10));

        popupStage.setScene(new Scene(root, 700, 520));
        popupStage.show();
    }

    /**
     * Létrehoz egy linket, amely a járat részletes információját jeleníti meg.
     * @param text a megjelenítendő szöveg
     * @param tripId a járat azonosítója
     * @param routeInfoDisplayer az információ megjelenítéséért felelős osztály
     * @return egy {@link Region}, amely vagy szimpla Label vagy kattintható Hyperlink (pl ha séta akk nem kattintható)
     */
    private static Region createTripLink(String text, String tripId, RouteInfoDisplayer routeInfoDisplayer) {
        if (tripId == null || tripId.isBlank()) return new Label(text);
        Hyperlink link = new Hyperlink(text);
        link.setBorder(Border.EMPTY);
        link.setPadding(Insets.EMPTY);
        link.setOnAction(e -> {
            try {
                routeInfoDisplayer.displayRouteInfo(convertTripId(tripId));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return link;
    }
    /**
     * Létrehoz egy kattintható hivatkozást, amely egy megállót indulási adatait nyitja meg
     * @param name a megálló neve
     * @param popupManager a popup kezelő komponens
     * @return egy {@link Hyperlink} komponens
     */

    private static Hyperlink createStopLink(String name, PopupManager popupManager) {
        Hyperlink link = new Hyperlink(name);
        link.setStyle("-fx-text-fill: #212121;");
        link.setOnAction(evt -> {
            var stopDto = new StopService().getStopByName(name);
            if (stopDto != null) {
                popupManager.loadAndShowStopPopup(stopDto.getId(), stopDto.getName(), stopDto.getLat(), stopDto.getLon());
            }
        });
        return link;
    }
    /**
     * Egy nyers trip ID-t konvertál BKK formátumúvá, ha szükséges
     * @param rawTripId a nyers ID
     * @return az átalakított ID
     */
    private static String convertTripId(String rawTripId) {
        return (rawTripId != null && rawTripId.startsWith("BKK:")) ? "BKK_" + rawTripId.substring(4) : rawTripId;
    }

    /**
     * formázza a közlekedési mód és járatszám alapján megjelenítendő szöveget
     * @param mode a közlekedési mód (BUS, TRAM, stb.)
     * @param label a címke/járat azonosító
     * @return járatleírás
     */
    private static String formatModeAndRoute(String mode, String label) {
        if (label == null) return mode;
        String[] parts = label.replace("[", "").replace("]", "").split(" ");
        if (parts.length < 2) return label;
        String route = parts[1].replace("BKK_", "");
        return switch (parts[0].toUpperCase()) {
            case "BUS" -> route + " busz";
            case "TRAM" -> route + " villamos";
            case "RAIL" -> route + " vonat";
            case "COACH" -> route + " busz";
            case "TROLLEYBUS" -> route + " trolibusz";
            default -> route;
        };
    }
    /**
     * Összefűzi az egymást követő séta szakaszokat egyetlen lépéssé
     * @param steps az eredeti lépések listája
     * @return az összevont lépések listája
     */
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
    /**
     * Létrehoz egy szakasz kártyát az adott lépéshez
     * @param step a lépés (PathStep)
     * @param index a lépés indexe a listában
     * @param popupManager a megállók popupjainak kezelője
     * @param routeInfoDisplayer a járatok részleteinek megjelenítője
     * @return egy VBox elem
     */
    private static VBox createStepCard(PathStep step, int index, PopupManager popupManager, RouteInfoDisplayer routeInfoDisplayer) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setSpacing(3);
        String backgroundColor = Colors.getBackGroundColor(step.getMode());
        card.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cccccc; -fx-background-color: " + backgroundColor + ";");
        Node headerContent = createStepHeader(step, routeInfoDisplayer);
        if (headerContent instanceof Labeled labeled) {
            labeled.setFont(Font.font("Arial", 16));
            labeled.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        }
        HBox headerBox = new HBox(headerContent);
        headerBox.setPadding(new Insets(6));
        HBox fromTo = new HBox(5);
        if (step.getMode().equalsIgnoreCase("WALK")) {
            if (index == 0) {
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
            LocalTime start = LocalTime.parse(step.getDeparture(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime end = LocalTime.parse(step.getArrival(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            long minutes = Duration.between(start, end).toMinutes();
            duration.setText((step.getMode().equalsIgnoreCase("WALK") && minutes == 0) ? "" :
                    (step.getMode().equalsIgnoreCase("WALK") ? "Séta idő: " : "Utazási idő: ") + minutes + " perc");
            duration.setFont(Font.font("Arial", 13));
        } catch (Exception ignored) {}
        card.getChildren().addAll(headerBox, fromTo, time, duration);
        return card;
    }
    /**
     * Létrehoz egy szakaszelválasztót
     * @return egy Label komponens "Következő szakasz" felirattal
     */
    private static Label createSectionSeparator() {
        Label label = new Label("Következő szakasz");
        label.setStyle("-fx-text-fill: gray;");
        label.setFont(Font.font("Arial", 12));
        return label;
    }
    /**
     * Elmenti az útvonalat egy PDF fájlba
     * @param routeLayout a teljes útvonal elrendezése (VBox)
     * @param owner a hívó ablak Stage-je, amelyhez a FileChooser kapcsolódik
     */
    private static void exportRouteAsPdf(VBox routeLayout, Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Útvonal mentése PDF-be");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF fájl", "*.pdf"));
        fileChooser.setInitialFileName("utvonal.pdf");

        File file = fileChooser.showSaveDialog(owner);
        if (file == null) return;

        try {
            WritableImage fxImage = routeLayout.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
            File tempImage = File.createTempFile("route_snapshot", ".png");
            ImageIO.write(bufferedImage, "png", tempImage);

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Image image = new Image(ImageDataFactory.create(tempImage.getAbsolutePath()));
            image.scaleToFit(pdf.getDefaultPageSize().getWidth() - 60, pdf.getDefaultPageSize().getHeight() - 60);
            document.add(image);
            document.close();

            tempImage.delete();

            new Alert(Alert.AlertType.INFORMATION, "PDF sikeresen elmentve:\n" + file.getAbsolutePath()).showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Hiba történt a PDF mentés során:\n" + ex.getMessage()).showAndWait();
        }
    }
    /**
     * Létrehozza egy lépés fejlécét (járat vagy séta megjelenítése, színezve).
     * @param step az adott lépés
     * @param routeInfoDisplayer a járat információ megjelenítéséért felelős komponens
     * @return egy Node, amely a fejléc lesz
     */
    private static Node createStepHeader(PathStep step, RouteInfoDisplayer routeInfoDisplayer) {
        String mode = step.getMode().toUpperCase();
        String icon = switch (mode) {
            case "BUS" -> "\uD83D\uDE8C ";
            case "TRAM" -> "\uD83D\uDE8A ";
            case "SUBWAY" -> "\uD83D\uDE87 ";
            case "WALK" -> "\uD83D\uDEB6 ";
            case "RAIL" -> "\uD83D\uDE82 ";
            case "COACH" -> "\uD83D\uDE8D ";
            case "TROLLEYBUS" -> "\uD83D\uDE8E";
            default -> "\u2B1B ";
        };

        String backgroundColor;
        String textColor;

        if (mode.equals("SUBWAY")) {
            String line = step.getLabel() != null ? step.getLabel().toUpperCase() : "";
            if (line.contains("M1")) {
                backgroundColor = Colors.getTitleColor("M1");
                textColor = Colors.getTextColor("M1");
            } else if (line.contains("M2")) {
                backgroundColor = Colors.getTitleColor("M2");
                textColor = Colors.getTextColor("M2");
            } else if (line.contains("M3")) {
                backgroundColor = Colors.getTitleColor("M3");
                textColor = Colors.getTextColor("M3");
            } else if (line.contains("M4")) {
                backgroundColor = Colors.getTitleColor("M4");
                textColor = Colors.getTextColor("M4");
            } else {
                backgroundColor = Colors.getTitleColor("SUBWAY");
                textColor = Colors.getTextColor("SUBWAY");
            }
        } else {
            backgroundColor = Colors.getTitleColor(mode);
            textColor = Colors.getTextColor(mode);
        }

        String labelText = step.getMode().equalsIgnoreCase("WALK")
                ? "Séta"
                : formatModeAndRoute(step.getMode(), step.getLabel());

        Node headerLabel = step.getMode().equalsIgnoreCase("WALK")
                ? new Label(icon + labelText)
                : createTripLink(icon + labelText, step.getTripId(), routeInfoDisplayer);

        headerLabel.setStyle("-fx-background-color: none; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + textColor);

        HBox headerBox = new HBox(headerLabel);
        headerBox.setPadding(new Insets(6));
        headerBox.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 8; -fx-border-radius: 8");

        return headerBox;
    }
    /**
     * Létrehoz egy ScrollPane-t, amely görgethetővé teszi az útvonalat.
     * @param content a megjelenítendő VBox tartalom
     * @return a ScrollPane komponens
     */

    private static ScrollPane createScrollableContent(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(600);
        return scroll;
    }
}
