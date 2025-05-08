package futar.futar.service;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import futar.futar.model.FavoriteRoute;
import futar.futar.model.FavoriteStop;

/**
 * A FavoriteManager osztály kezeli a kedvenc megállók és útvonalak tárolását és elérését.
 * Singleton minta alapján működik, és JSON fájlba menti az adatokat.
 */
public class FavoriteManager {
    private static FavoriteManager instance;

    private static final Path PATH = Paths.get("favorites.json");
    private List<FavoriteStop> favoriteStops = new ArrayList<>();
    private List<FavoriteRoute> favoriteRoutes = new ArrayList<>();
    /**
     * Privát konstruktor, amely automatikusan betölti a mentett adatokat.
     */
    private FavoriteManager() {
        load(); // automatikus betöltés
    }

    /**
     * Singleton példány lekérdezése.
     *
     * @return a FavoriteManager egyetlen példánya
     */
    public static FavoriteManager getInstance() {
        if (instance == null) {
            synchronized (FavoriteManager.class) {
                if (instance == null) {
                    instance = new FavoriteManager();
                }
            }
        }
        return instance;
    }
    /**
     * Betölti a kedvenc megállókat és útvonalakat a JSON fájlból, ha az létezik.
     */
    public void load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                favoriteStops = new Gson().fromJson(root.get("stops"), new TypeToken<List<FavoriteStop>>(){}.getType());
                favoriteRoutes = new Gson().fromJson(root.get("routes"), new TypeToken<List<FavoriteRoute>>(){}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Elmenti a kedvenc megállókat és útvonalakat a JSON fájlba.
     */
    public void save() {
        JsonObject root = new JsonObject();
        root.add("stops", new Gson().toJsonTree(favoriteStops));
        root.add("routes", new Gson().toJsonTree(favoriteRoutes));

        try (Writer writer = Files.newBufferedWriter(PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Megvizsgálja, hogy a megadott megálló szerepel-e a kedvencek között.
     *
     * @param stopId a megálló azonosítója
     * @return true, ha kedvenc, különben false
     */

    public Boolean isFavoriteStop(String stopId) {
        return favoriteStops.stream().anyMatch(stop -> stop.getStopId().equals(stopId));
    }
    /**
     * Lekéri az összes kedvenc megállót.
     *
     * @return a kedvenc megállók listája
     */

    public List<FavoriteStop> getFavoriteStops() { return favoriteStops; }
    /**
     * Lekéri az összes kedvenc útvonalat.
     *
     * @return a kedvenc útvonalak listája
     */

    public List<FavoriteRoute> getFavoriteRoutes() { return favoriteRoutes; }
    /**
     * Hozzáad egy új megállót a kedvencekhez, majd elmenti az adatokat.
     *
     * @param stop a hozzáadandó megálló
     */

    public void addStop(FavoriteStop stop) {
        favoriteStops.add(stop);
        save();
    }
    /**
     * Hozzáad egy új útvonalat a kedvencekhez, majd elmenti az adatokat.
     *
     * @param route a hozzáadandó útvonal
     */

    public void addRoute(FavoriteRoute route) {
        favoriteRoutes.add(route);
        save();
    }
    /**
     * Eltávolít egy megállót a kedvencek közül azonosító alapján, majd elmenti az adatokat.
     *
     * @param stopId a törlendő megálló azonosítója
     */

    public void removeStop(String stopId) {
        favoriteStops.removeIf(stop -> stop.getStopId().equals(stopId));
        save();
    }
    /**
     * Eltávolít egy útvonalat a kedvencek közül a kiinduló és célmegálló alapján.
     *
     * @param from a kiinduló megálló neve
     * @param to   a célmegálló neve
     */

    public void removeRoute(String from, String to) {
        favoriteRoutes.removeIf(route -> route.getFromStop().equals(from) && route.getToStop().equals(to));
        save();
    }
}
