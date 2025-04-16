package futar.futar.controller;
import futar.futar.api.ApiClientProvider;
import futar.futar.api.TripApi;
import futar.futar.model.DepartureDTO;
import futar.futar.model.StopDTO;
import futar.futar.service.DepartureService;
import futar.futar.service.StopService;
import futar.futar.view.DepartureViewBuilder;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import futar.futar.service.FavoriteManager;
import futar.futar.model.FavoriteStop;

import java.net.URL;
import java.util.List;
import java.util.Optional;


public class MapController {
    private String lastStopId;
    private String lastStopName;
    private String selectedStopId;
    private String selectedStopName;
    URL defaultMarkerUrl = getClass().getResource("/html/icons/marker-on-map.png");
    URL selectedMarkerUrl = getClass().getResource("/html/icons/marker-on-map-selected.png");

    private final WebEngine webEngine;
    private final StopService stopService = new StopService();
    private final FavoriteManager favoriteManager = new FavoriteManager();
    public MapController(javafx.scene.web.WebView mapView) {
        this.webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/html/map.html").toExternalForm());
        Platform.runLater(() -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
        });

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");

                // EDDIG: window.setMember("java", this);
                // HELYETTE:
                if (javaConnector != null) {
                    window.setMember("java", javaConnector);
                    System.out.println("Java objektum átadva a JavaScript-nek");
                } else {
                    System.out.println("❌ Java objektum nincs beállítva");
                }
            }
        });

        this.favoriteManager.load();
    }

    public void logFromJavaScript(String message) {
        System.out.println("Received from JS: " + message);
    }
    public void handleStopDetails(String stopId, String name, double lat, double lon) {
        lastStopId = stopId;
        lastStopName = name;
        selectedStopId = stopId;
        selectedStopName = name;

        new Thread(() -> {
            try {
                List<DepartureDTO> departures = new DepartureService(
                        new org.openapitools.client.api.DefaultApi(ApiClientProvider.getClient()))
                        .getDepartures(stopId);

                if (departures.isEmpty()) {
                    departures = new DepartureService(
                            new org.openapitools.client.api.DefaultApi(ApiClientProvider.getClient()))
                            .getNearbyDepartures(lat, lon);
                    System.out.println("Üres");
                }

                // 💡 Ezt emeld ki itt, hogy használható legyen lambdában
                String buttonText = favoriteManager.isFavoriteStop(stopId)
                        ? "Törlés a kedvencekből"
                        : "Kedvenc";

                String finalHtml = DepartureViewBuilder.build(departures, buttonText);

                Platform.runLater(() -> {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.call("updatePopupContent", name, finalHtml);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Object javaConnector;

    public void setJavaConnector(Object connector) {
        this.javaConnector = connector;
    }
    public void showMultipleStopsOnMap(List<StopDTO> stops, boolean openPopup) {
        if (stops == null || stops.isEmpty()) return;

        clearAndShowStops(stops);
        StopDTO first = stops.get(0);

        Platform.runLater(() -> {
            webEngine.executeScript(String.format("focusOn(%f, %f)", first.getLat(), first.getLon()));

            if (openPopup) {
                String script = String.format("""
                var targetMarker = allStops.find(m =>
                    m.getLatLng().lat === %f && m.getLatLng().lng === %f
                );
                if (targetMarker) {
                    if (selectedMarker && selectedMarker !== targetMarker) {
                        selectedMarker.setIcon(defaultIcon);
                    }
                    selectedMarker = targetMarker;
                    selectedMarker.setIcon(highlightedIcon);
                    selectedMarker.openPopup(); 
                }
            """, first.getLat(), first.getLon());
                webEngine.executeScript(script);
            }
        });
    }






    public void clearAndShowStops(List<StopDTO> stops) {
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.call("clearStops");
        for (StopDTO stop : stops) {
            window.call("addStopTracked", stop.getLat(), stop.getLon(), stop.getName(), stop.getId());
        }

    }

    public void addFavoriteStop() {
        if (lastStopId == null || lastStopName == null) return;

        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Név megadása");
            dialog.setHeaderText("Adj nevet a kedvencnek");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                favoriteManager.addStop(new FavoriteStop(name, lastStopId, lastStopName));
                System.out.println("✅ Kedvencként mentve: " + name);
            });
        });
    }

    public void toggleFavorite() {
        if (selectedStopId == null || selectedStopName == null) return;

        boolean alreadyFavorite = favoriteManager.isFavoriteStop(selectedStopId);

        if (alreadyFavorite) {
            favoriteManager.removeStop(selectedStopId);
            updateFavoriteButtonText("Kedvenc");
        } else {
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog(selectedStopName); // <- itt beállítjuk alapnak a nevet
                dialog.setTitle("Név megadása");
                dialog.setHeaderText("Adj nevet a kedvencnek");
                dialog.setContentText("Név:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(name -> {
                    favoriteManager.addStop(new FavoriteStop(name, selectedStopId, selectedStopName));
                    updateFavoriteButtonText("Törlés a kedvencekből");
                });
            });
        }
    }



    private void updateFavoriteButtonText(String text) {
        Platform.runLater(() -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
            webEngine.executeScript("document.getElementById('favoriteButton').innerText = '" + text + "'");
        });
    }


    public FavoriteManager getFavoriteManager() {
        return favoriteManager;
    }

    public void showRouteDetails(String routeId) {
    }



    public void handleRouteClick(String tripId) {
        new Thread(() -> {
            try {
                TripApi tripApi = new TripApi();
                List<StopDTO> stops = tripApi.getStopsByTrip(tripId);

                Platform.runLater(() -> {
                    showRouteInfoPopup(tripId, stops); // ez külön popupban mutatja a részleteket
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void showRouteInfoPopup(String routeId, List<StopDTO> stops) {
        String routeName = "Járat " + routeId; // később majd lehet finomítani pl. destination alapján
        String html = futar.futar.view.RouteViewBuilder.build(routeName, stops);

        if (stops.isEmpty()) return;

        StopDTO firstStop = stops.get(0);

        Platform.runLater(() -> {
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.call("updatePopupContent", routeName, html);
            //webEngine.executeScript(String.format("focusOn(%f, %f)", firstStop.getLat(), firstStop.getLon()));
        });
    }



}
