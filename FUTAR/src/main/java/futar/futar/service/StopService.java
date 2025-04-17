package futar.futar.service;

import futar.futar.api.ApiClientProvider;
import futar.futar.api.StopApi;
import futar.futar.api.TripApi;
import futar.futar.model.StopDTO;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.StopsForLocationResponse;
import org.openapitools.client.model.TransitStop;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StopService {
    private final StopApi stopApi;
    private final TripApi tripApi;

    public StopService() {
        this.stopApi = new StopApi();
        this.tripApi = new TripApi();
    }

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

    public List<StopDTO> getStopsByTripId(String tripId) {
        try {
            return tripApi.getStopsByTrip(tripId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<StopDTO> getNearbyStopsByName(String name, double radiusMeters) {
        StopDTO center = getStopByName(name);
        if (center == null) return List.of();

        List<StopDTO> nearby = getStops(center.getLat(), center.getLon());

        return nearby.stream()
                .filter(stop -> calculateDistance(center.getLat(), center.getLon(), stop.getLat(), stop.getLon()) <= radiusMeters)
                .collect(Collectors.toList());
    }

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

    public List<StopDTO> getNearbyStopsIncludingCenter(String name, double radiusMeters) {
        StopDTO center = getStopByName(name);
        if (center == null) return List.of();

        List<StopDTO> nearby = getStops(center.getLat(), center.getLon());

        return nearby.stream()
                .filter(stop -> calculateDistance(center.getLat(), center.getLon(), stop.getLat(), stop.getLon()) <= radiusMeters)
                .collect(Collectors.toList());
    }
}
