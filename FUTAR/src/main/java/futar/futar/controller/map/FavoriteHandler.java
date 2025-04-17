package futar.futar.controller.map;

import futar.futar.model.FavoriteStop;
import futar.futar.service.FavoriteManager;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class FavoriteHandler {
    private final FavoriteManager favoriteManager;
    private String lastStopId;
    private String lastStopName;
    private String selectedStopId;
    private String selectedStopName;

    public FavoriteHandler(FavoriteManager favoriteManager) {
        this.favoriteManager = favoriteManager;
    }

    public void promptAndAddFavoriteStop() {
        if (lastStopId == null || lastStopName == null) return;
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Név megadása");
            dialog.setHeaderText("Adj nevet a kedvencnek");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                favoriteManager.addStop(new FavoriteStop(name, lastStopId, lastStopName));
            });
        });
    }

    public void toggleFavorite() {
        if (selectedStopId == null || selectedStopName == null) return;
        boolean alreadyFavorite = favoriteManager.isFavoriteStop(selectedStopId);
        if (alreadyFavorite) {
            favoriteManager.removeStop(selectedStopId);
        } else {
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog(selectedStopName);
                dialog.setTitle("Név megadása");
                dialog.setHeaderText("Adj nevet a kedvencnek");
                dialog.setContentText("Név:");
                dialog.showAndWait().ifPresent(name -> {
                    favoriteManager.addStop(new FavoriteStop(name, selectedStopId, selectedStopName));
                });
            });
        }
    }

    public FavoriteManager getFavoriteManager() {
        return favoriteManager;
    }

    public void setSelectedStop(String stopId, String name) {
        this.selectedStopId = stopId;
        this.selectedStopName = name;
    }
}