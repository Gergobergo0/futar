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

public class FavoritesDialogBuilder {
    private static void showFavoriteStop(FavoriteStop stop, FavoriteManager favoriteManager, PopupManager popupManager, StopMarkerDisplayer stopMarkerDisplayer, Dialog<?> dialog) {
        StopDTO stopDto = new StopService().getStopByName(stop.getStopName());
        if (stopDto != null) {
            popupManager.setSelectedStop(stopDto.getId(), stopDto.getName());
            stopMarkerDisplayer.clearMap();
            popupManager.clearFloatingPopup();
            stopMarkerDisplayer.showMultipleStops(List.of(stopDto), true);
            popupManager.showDepartures(stopDto.getId(), stopDto.getName(), stopDto.getLat(), stopDto.getLon());
            dialog.close();
        }
    }

    public static Dialog<Void> build(FavoriteManager favoriteManager, Runnable onRefresh, PopupManager popupManager, StopMarkerDisplayer stopMarkerDisplayer) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("⭐ Kedvencek");
        dialog.setHeaderText("Kedvenc megállók és útvonalak");

        VBox content = new VBox(10);

        // 📍 Kedvenc megállók
        Label stopLabel = new Label("📍 Megállók:");
        content.getChildren().add(stopLabel);

        List<FavoriteStop> stops = favoriteManager.getFavoriteStops();
        for (FavoriteStop stop : stops) {
            HBox row = new HBox(10);
            Hyperlink name = new Hyperlink(stop.getName());
            name.setOnAction(e -> showFavoriteStop(stop, favoriteManager, popupManager, stopMarkerDisplayer, dialog));

            Button rename = new Button("✏️");
            Button delete = new Button("❌");

            rename.setOnAction(e -> {
                TextInputDialog renameDialog = new TextInputDialog(stop.getName());
                renameDialog.setHeaderText("Megálló átnevezése");
                renameDialog.setContentText("Új név:");
                renameDialog.showAndWait().ifPresent(newName -> {
                    stop.setName(newName);
                    favoriteManager.save();
                    onRefresh.run();
                    dialog.close();
                });
            });

            delete.setOnAction(e -> {
                favoriteManager.removeStop(stop.getStopId());
                onRefresh.run();
                dialog.close();
            });

            row.getChildren().addAll(name, rename, delete);
            content.getChildren().add(row);
        }

        // 🚏 Kedvenc útvonaltervek
        Label routeLabel = new Label("🚏 Útvonaltervek:");
        content.getChildren().add(routeLabel);

        List<FavoriteRoute> routes = favoriteManager.getFavoriteRoutes();
        for (FavoriteRoute route : routes) {
            HBox row = new HBox(10);
            Label name = new Label(route.getName() + " (" + route.getFromStop() + " → " + route.getToStop() + ")");
            Button rename = new Button("✏️");
            Button delete = new Button("❌");

            rename.setOnAction(e -> {
                TextInputDialog renameDialog = new TextInputDialog(route.getName());
                renameDialog.setHeaderText("Útvonal átnevezése");
                renameDialog.setContentText("Új név:");
                renameDialog.showAndWait().ifPresent(newName -> {
                    route.setName(newName);
                    favoriteManager.save();
                    onRefresh.run();
                    dialog.close();
                });
            });

            delete.setOnAction(e -> {
                favoriteManager.removeRoute(route.getFromStop(), route.getToStop());
                onRefresh.run();
                dialog.close();
            });

            row.getChildren().addAll(name, rename, delete);
            content.getChildren().add(row);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        return dialog;
    }

    // ÚJ metódus a FavoritesDialogBuilder-ben

    public static void refreshContent(VBox container, FavoriteManager favoriteManager, Runnable onRefresh, PopupManager popupManager, Dialog<?> dialog, RoutePlannerController routePlannerController) {
        container.getChildren().clear();

        Label stopLabel = new Label("📍 Megállók:");
        container.getChildren().add(stopLabel);

        for (FavoriteStop stop : favoriteManager.getFavoriteStops()) {
            HBox row = new HBox(10);
            Hyperlink name = new Hyperlink(stop.getName());
            name.setOnAction(e -> {
                StopDTO stopDto = new StopService().getStopByName(stop.getStopName());
                if (stopDto != null) {
                    popupManager.showFloatingPopupForStop(
                            stopDto.getId(),
                            stopDto.getName(),
                            stopDto.getLat(),
                            stopDto.getLon()
                    );
                    dialog.close();
                }
            });


            Button rename = new Button("✏️");
            Button delete = new Button("❌");

            rename.setOnAction(e -> {
                TextInputDialog renameDialog = new TextInputDialog(stop.getName());
                renameDialog.setHeaderText("Megálló átnevezése");
                renameDialog.setContentText("Új név:");
                renameDialog.showAndWait().ifPresent(newName -> {
                    stop.setName(newName);
                    favoriteManager.save();
                    refreshContent(container, favoriteManager, onRefresh, popupManager, dialog, routePlannerController);
                });
            });

            delete.setOnAction(e -> {
                favoriteManager.removeStop(stop.getStopId());
                refreshContent(container, favoriteManager, onRefresh, popupManager, dialog, routePlannerController);
            });

            row.getChildren().addAll(name, rename, delete);
            container.getChildren().add(row);
        }

        Label routeLabel = new Label("🚏 Útvonaltervek:");
        container.getChildren().add(routeLabel);

        for (FavoriteRoute route : favoriteManager.getFavoriteRoutes()) {
            HBox row = new HBox(10);
            Hyperlink name = new Hyperlink(route.getName() + " (" + route.getFromStop() + " → " + route.getToStop() + ")");
            name.setOnAction(e -> {
                routePlannerController.planFavoriteRoute(route.getFromStop(), route.getToStop());
                dialog.close(); // bezárjuk az ablakot
            });
            Button rename = new Button("✏️");
            Button delete = new Button("❌");

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

}
