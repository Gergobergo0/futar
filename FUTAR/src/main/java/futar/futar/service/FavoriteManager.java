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
public class FavoriteManager {
    private static final Path PATH = Paths.get("favorites.json");
    private List<FavoriteStop> favoriteStops = new ArrayList<>();
    private List<FavoriteRoute> favoriteRoutes = new ArrayList<>();

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

    public Boolean isFavoriteStop(String stopId) {
        return favoriteStops.stream()
                .anyMatch(stop -> stop.getStopId().equals(stopId));
    }


    public List<FavoriteStop> getFavoriteStops() { return favoriteStops; }
    public List<FavoriteRoute> getFavoriteRoutes() { return favoriteRoutes; }

    public void addStop(FavoriteStop stop) {
        favoriteStops.add(stop);
        save();
    }

    public void addRoute(FavoriteRoute route) {
        favoriteRoutes.add(route);
        save();
    }

    public void removeStop(String stopId) {
        favoriteStops.removeIf(stop -> stop.getStopId().equals(stopId));
        save();
    }

    public void removeRoute(String from, String to) {
        favoriteRoutes.removeIf(route ->
                route.getFromStop().equals(from) && route.getToStop().equals(to)
        );
        save();
    }




}