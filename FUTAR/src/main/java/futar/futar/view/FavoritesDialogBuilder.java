package futar.futar.view;

import futar.futar.controller.RoutePlannerController;
import futar.futar.controller.map.PopupManager;
import futar.futar.controller.map.StopMarkerDisplayer;
import futar.futar.model.FavoriteRoute;
import futar.futar.model.FavoriteStop;
import futar.futar.model.StopDTO;
import futar.futar.service.FavoriteManager;
import futar.futar.service.StopService;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * A javafx kedvencek ablakát hozza létre, megállókat, útvonalakat jelenít meg, biztosítja h ezok kattinthatók legyenek
 */
public class FavoritesDialogBuilder {
    private static StopService stopService = new StopService();

    /**
     * létrehoz egy javaFX ablakot, amiben megjelennek a kedvenc megállók, útvonalak
     * @param favoriteManager kedvenc útvonalakat, megállókat kezelő osztály
     * @param onRefresh frissítési callback, ami akkor hívódik meg ha változás történik
     * @param popupManager  popupokat kezelő objektum
     * @param stopMarkerDisplayer   térképen megjelenítő markereket kezelő osztály
     * @return egy {@link Dialog} példány, amely megjeleníti a kedvenceket
     */
    public static Dialog<Void> build(FavoriteManager favoriteManager, Runnable onRefresh, PopupManager popupManager, StopMarkerDisplayer stopMarkerDisplayer) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Kedvencek");
        dialog.setHeaderText("Kedvenc megállók és útvonalak");

        VBox content = new VBox(10);

        Label stopLabel = new Label("Megállók:");
        content.getChildren().add(stopLabel);

        for (FavoriteStop stop : favoriteManager.getFavoriteStops()) {
            content.getChildren().add(
                    buildStopRow(stop, favoriteManager, popupManager, stopMarkerDisplayer, dialog, onRefresh, content, null, false)
            );
        }


        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        return dialog;
    }

    /**
     * Frissíti a VBOX tartalmát a jelenlegi kedvencek alapján
     * újratölti a kedvenc útvonalak és megállókat
     * @param container frissítendő VBOX
     * @param favoriteManager a kedvenc megállók és útvonalak kezelője
     * @param onRefresh callback függvény frissítéshez újramegjelenítéshez
     * @param popupManager a popup megjelenítést kezelő komponens
     * @param dialog a dialógus, amely frissítés után is aktív marad
     * @param routePlannerController az útvonaltervező vezérlő, amely egy útvonal kiválasztása esetén meghívódik
     */
    public static void refreshContent(VBox container, FavoriteManager favoriteManager, Runnable onRefresh, PopupManager popupManager, Dialog<?> dialog, RoutePlannerController routePlannerController) {
        container.getChildren().clear();

        Label stopLabel = new Label("Megállók:");
        container.getChildren().add(stopLabel);

        for (FavoriteStop stop : favoriteManager.getFavoriteStops()) {
            container.getChildren().add(
                    buildStopRow(stop, favoriteManager, popupManager, null, dialog, onRefresh, container, routePlannerController, true)
            );
        }


        Label routeLabel = new Label("Útvonaltervek:");
        container.getChildren().add(routeLabel);

        for (FavoriteRoute route : favoriteManager.getFavoriteRoutes()) {
            HBox row = new HBox(10);
            Hyperlink name = new Hyperlink(route.getName() + " (" + route.getFromStop() + " → " + route.getToStop() + ")");
            name.setOnAction(e -> {
                routePlannerController.planFavoriteRoute(route.getFromStop(), route.getToStop());
                dialog.close(); // bezárjuk az ablakot
            });
            Button rename = new Button("Szerkesztés");
            Button delete = new Button("Törlés");

            rename.setOnAction(e -> {
                TextInputDialog renameDialog = new TextInputDialog(route.getName());
                renameDialog.setHeaderText("Útvonal átnevezése");
                renameDialog.setContentText("Új név:");
                renameDialog.showAndWait().ifPresent(newName -> {
                    route.setName(newName);
                    favoriteManager.save();
                    refreshContent(container, favoriteManager, onRefresh, popupManager, dialog, routePlannerController);
                });
            });

            delete.setOnAction(e -> {
                favoriteManager.removeRoute(route.getFromStop(), route.getToStop());
                refreshContent(container, favoriteManager, onRefresh, popupManager, dialog, routePlannerController);
            });

            row.getChildren().addAll(name, rename, delete);
            container.getChildren().add(row);
        }
    }

    /**
     * kedvenc megállóhoz tartozó HBOX sor
     * Kattintható a megálló, szerkeszthető, törölhető
     * @param stop a megjelenítendő kedvenc megálló
     * @param favoriteManager a kedvenceket kezelő osztály
     * @param popupManager popupok kezelése megálló megnyitásához
     * @param stopMarkerDisplayer a térképen való megjelenítésért felelős komponens
     * @param dialog a teljes dialógus referenciája
     * @param onRefresh frissítési callback (ha nem újratöltjük a konténert)
     * @param container a VBox, ha újratöltésre van szükség
     * @param routePlannerController opcionálisan átadott útvonalvezérlő
     * @param refreshInsteadOfClose true esetén a dialógus újratölt, nem záródik be (útvonalnézetnél hasznos)
     * @return egy sor HBox, amely megjeleníti a kedvenc megállót és annak vezérlőgombjait
     */
    private static HBox buildStopRow(
            FavoriteStop stop,
            FavoriteManager favoriteManager,
            PopupManager popupManager,
            StopMarkerDisplayer stopMarkerDisplayer,
            Dialog<?> dialog,
            Runnable onRefresh,
            VBox container,
            RoutePlannerController routePlannerController,
            boolean refreshInsteadOfClose
    ) {
        HBox row = new HBox(10);
        Hyperlink name = new Hyperlink(stop.getName());

        name.setOnAction(e -> {
            StopDTO stopDto = stopService.getStopByName(stop.getStopName());
            if (stopDto != null) {
                popupManager.loadAndShowStopPopup(
                        stopDto.getId(),
                        stopDto.getName(),
                        stopDto.getLat(),
                        stopDto.getLon()
                );
                if (!refreshInsteadOfClose) {
                    stopMarkerDisplayer.clearMap();
                    popupManager.clearFloatingPopup();
                    stopMarkerDisplayer.showMultipleStops(List.of(stopDto), true);
                    popupManager.showDepartures(stopDto.getId(), stopDto.getName(), stopDto.getLat(), stopDto.getLon());
                    dialog.close();
                }
            }
        });

        Button rename = new Button("Szerkesztés");
        rename.setOnAction(e -> {
            TextInputDialog renameDialog = new TextInputDialog(stop.getName());
            renameDialog.setHeaderText("Megálló átnevezése");
            renameDialog.setContentText("Új név:");
            renameDialog.showAndWait().ifPresent(newName -> {
                stop.setName(newName);
                favoriteManager.save();
                if (refreshInsteadOfClose) {
                    refreshContent(container, favoriteManager, onRefresh, popupManager, dialog, routePlannerController);
                } else {
                    onRefresh.run();
                    dialog.close();
                }
            });
        });

        Button delete = new Button("Törlés");
        delete.setOnAction(e -> {
            favoriteManager.removeStop(stop.getStopId());
            if (refreshInsteadOfClose) {
                refreshContent(container, favoriteManager, onRefresh, popupManager, dialog, routePlannerController);
            } else {
                onRefresh.run();
                dialog.close();
            }
        });

        row.getChildren().addAll(name, rename, delete);
        return row;
    }


}
