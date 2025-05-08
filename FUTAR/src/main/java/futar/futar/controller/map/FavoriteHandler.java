package futar.futar.controller.map;

import futar.futar.model.FavoriteStop;
import futar.futar.service.FavoriteManager;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * kezeli a megállók kedvencéhez adását és eltávolítást a térképes nézetben
 * <p>
 *     Ez az osztály bizotsítja h a felhasználó egy megállót elmentsen/eltávolítson a kedvencek közül
 * </p>
 */
public class FavoriteHandler {
    /**
     * kedvencekek kezelő osztály
     */
    private final FavoriteManager favoriteManager;
    /**
     * kiválasztott megálló azonosítója
     */
    private String selectedStopId;
    /**
     * kiválasztott megálló neve
     */
    private String selectedStopName;
    /**
     * visszahívás függvény, kedvencek módosítása után hívja meg a program
     * alapértelmezettem semmi
     */
    private Runnable refreshCallback = () -> {};

    /**
     * létrehoz egy új {@code FavoriteHandler} példányt
     * @param favoriteManager kedvencek kezeléséért felelős osztály
     */
    public FavoriteHandler(FavoriteManager favoriteManager) {

        this.favoriteManager = favoriteManager;

    }

    /**
     * Be/kikapcsolja a kiválasztott megállót a kedvencek listájában.
     * <p>
     * Ha már kedvenc, eltávolítja, ha nem, megkéri a felhasználót egy név megadására, majd elmenti
     * At végén meghívja a {@link #setRefreshCallback(Runnable)} frissítési függvényt
     */
    public void toggleFavorite() {
        System.out.println("[JAVA] toggleFavorite() meghívva");
        if (selectedStopId == null || selectedStopName == null) return;
        boolean alreadyFavorite = favoriteManager.isFavoriteStop(selectedStopId);

        if (alreadyFavorite) {
            favoriteManager.removeStop(selectedStopId);
            refreshCallback.run(); //frisítés törlés után
        } else {
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog(selectedStopName);
                dialog.setTitle("Név megadása");
                dialog.setHeaderText("Adj nevet a kedvencnek");
                dialog.setContentText("Név:");
                dialog.showAndWait().ifPresent(name -> {
                    favoriteManager.addStop(new FavoriteStop(name, selectedStopId, selectedStopName));
                    refreshCallback.run(); //frissítés hozzáadás után
                });
            });
        }
    }


    /**
     * getter
     * @return {@link FavoriteManager} példány
     */
    public FavoriteManager getFavoriteManager() {
        return favoriteManager;
    }

    /**
     * Beállítja az aktuálisan kiválasztott megállót
     * @param stopId megálló azonosítója
     * @param name megálló neve
     */
    public void setSelectedStop(String stopId, String name) {
        this.selectedStopId = stopId;
        this.selectedStopName = name;
    }

    /**
     * beállítja a frissítéshez használt visszahívási függvényt ha módosítás történt
     * @param callback futtatandó művelet
     */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }
}