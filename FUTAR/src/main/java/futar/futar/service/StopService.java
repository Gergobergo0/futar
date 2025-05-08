package futar.futar.service;

import futar.futar.api.StopApi;
import futar.futar.api.TripApi;
import futar.futar.model.StopDTO;
import org.openapitools.client.model.StopsForLocationResponse;
import org.openapitools.client.model.TransitStop;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/**
 * A StopService osztály a megállók lekérdezéséért felelős.
 * Képes megállók keresésére név vagy földrajzi pozíció alapján,
 * illetve útvonalakhoz tartozó megállólisták lekérdezésére.
 */
public class StopService {
    private final StopApi stopApi;
    private final TripApi tripApi;
    /**
     * Konstruktor, amely inicializálja a StopApi és TripApi példányokat.
     */

    public StopService() {
        this.stopApi = new StopApi();
        this.tripApi = new TripApi();
    }
    /**
     * Megjeleníti a konzolon az adott koordináták közelében található megállók nevét és pozícióját.
     *
     * @param lat szélességi koordináta
     * @param lon hosszúsági koordináta
     */

    public void printStops(double lat, double lon) {
        try {
            StopsForLocationResponse response = stopApi.getStopsNear(lat, lon, 500);
            response.getData().getList().forEach(stop ->
                    System.out.println(stop.getName() + " (" + stop.getLat() + ", " + stop.getLon() + ")")
            );
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
        }
    }
    /**
     * Lekéri az adott koordináták körüli megállókat 500 méteres körzetben.
     *
     * @param lat szélességi koordináta
     * @param lon hosszúsági koordináta
     * @return a környező megállók listája {@link StopDTO} formában
     */

    public List<StopDTO> getStops(double lat, double lon) {
        try {
            StopsForLocationResponse response = stopApi.getStopsNear(lat, lon, 500);

            return response.getData().getList().stream()
                    .map(stop -> new StopDTO(stop.getName(), stop.getLat(), stop.getLon(), stop.getId()))
                    .toList();
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
            return List.of();
        }
    }
    /**
     * Megállóazonosító lekérdezése egy megállónév alapján.
     *
     * @param name a megálló neve
     * @return az első találat ID-ja, vagy null, ha nem található
     */

    public String getStopIdByName(String name) {
        try {
            List<StopDTO> stops = getStopsByName(name);
            if (!stops.isEmpty()) {
                return stops.get(0).getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private StopDTO mapToDTO(TransitStop stop) {
        return new StopDTO(stop.getName(), stop.getLat(), stop.getLon(), stop.getId());
    }
    /**
     * Lekéri az első megállót, amely megfelel a megadott névnek.
     *
     * @param name a megálló neve
     * @return az első egyező {@link StopDTO}, vagy null
     */

    public StopDTO getStopByName(String name) {
        try {
            List<StopDTO> stops = getStopsByName(name);
            if (!stops.isEmpty()) {
                return stops.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Megállók listája név alapján.
     *
     * @param query keresési kifejezés (megállónév)
     * @return a találatok listája {@link StopDTO} formában
     */

    public List<StopDTO> getStopsByName(String query) {
        try {
            return stopApi.getStopsByName(query).getData().getList().stream()
                    .map(this::mapToDTO)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    /**
     * Lekéri egy járathoz tartozó megállókat.
     *
     * @param tripId a járat azonosítója
     * @return megállók listája az adott járathoz
     */

    public List<StopDTO> getStopsByTripId(String tripId) {
        try {
            return tripApi.getStopsByTrip(tripId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    /**
     * Lekéri a megadott nevű megálló körüli megállókat adott sugarú körben.
     *
     * @param name         középpont megálló neve
     * @param radiusMeters sugár méterben
     * @return közeli megállók listája
     */

    public List<StopDTO> getNearbyStopsByName(String name, double radiusMeters) {
        StopDTO center = getStopByName(name);
        if (center == null) return List.of();

        List<StopDTO> nearby = getStops(center.getLat(), center.getLon());

        return nearby.stream()
                .filter(stop -> calculateDistance(center.getLat(), center.getLon(), stop.getLat(), stop.getLon()) <= radiusMeters)
                .collect(Collectors.toList());
    }
    /**
     * Kiszámítja két földrajzi pont közötti távolságot méterben.
     *
     * @param lat1 első pont szélességi foka
     * @param lon1 első pont hosszúsági foka
     * @param lat2 második pont szélességi foka
     * @param lon2 második pont hosszúsági foka
     * @return távolság méterben
     */

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    /**
     * Lekéri a középpontot is tartalmazó közeli megállók listáját.
     *
     * @param name         középpont megálló neve
     * @param radiusMeters sugár méterben
     * @return közeli megállók listája a középponttal együtt
     */

    public List<StopDTO> getNearbyStopsIncludingCenter(String name, double radiusMeters) {
        StopDTO center = getStopByName(name);
        if (center == null) return List.of();

        List<StopDTO> nearby = getStops(center.getLat(), center.getLon());

        return nearby.stream()
                .filter(stop -> calculateDistance(center.getLat(), center.getLon(), stop.getLat(), stop.getLon()) <= radiusMeters)
                .collect(Collectors.toList());
    }
    /**
     * Visszaadja egy megálló koordinátáit (latitude,longitude) formátumban.
     *
     * @param stopName a megálló neve
     * @return koordináták szövegként vagy null
     */

    public String getCoordinatesByStopName(String stopName) {
        StopDTO stop = getStopByName(stopName);

        if (stop != null) {
            return stop.getLat() + "," + stop.getLon();
        }
        return null;
    }
    /**
     * OpenTripPlanner-kompatibilis koordináta formátumot ad vissza.
     *
     * @param stopName a megálló neve
     * @return koordináta szöveg "lat,lon" formátumban vagy null
     */

    public String getOtpCompatibleCoordinates(String stopName) {
        StopDTO stop = getStopsByName(stopName).stream().findFirst().orElse(null);
        if (stop == null) return null;
        return stop.getLat() + "," + stop.getLon();
    }


}