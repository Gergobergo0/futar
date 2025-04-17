package futar.futar.model;

import java.util.List;

public class RoutePath {
    private final String tripId;
    private final List<StopDTO> stops;

    public RoutePath(String tripId, List<StopDTO> stops) {
        this.tripId = tripId;
        this.stops = stops;
    }

    public String getTripId() { return tripId; }
    public List<StopDTO> getStops() { return stops; }
}
