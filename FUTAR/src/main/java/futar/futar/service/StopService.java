package futar.futar.service;
import futar.futar.api.ApiClientProvider;
import futar.futar.model.StopDTO;
import futar.futar.api.StopApi;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.StopsForLocationResponse;
import org.openapitools.client.model.TransitStop;

import java.util.Collections;
import java.util.List;

public class StopService {
    private final StopApi stopApi;

    public StopService()
    {
        this.stopApi = new StopApi();
    }

    public void printStops(double lat, double lon) {
        try {
            StopsForLocationResponse response = stopApi.getStopsNear(lat, lon, 500);
            response.getData().getList().forEach(stop ->
                    System.out.println(stop.getName() + " (" + stop.getLat() + ", " + stop.getLon() + ")")
            );
            List<StopDTO> simplified = response.getData().getList().stream()
                    .map(stop -> new StopDTO(stop.getName(), stop.getLat(), stop.getLon(), stop.getId()))
                    .toList();

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
            return List.of(); // üres lista hiba esetén
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


}
