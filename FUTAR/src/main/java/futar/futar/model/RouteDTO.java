package futar.futar.model;

import java.util.List;

public class RouteDTO {
    private String id;
    private String name;
    private String headsign;
    private List<StopDTO> stops;

    public RouteDTO(String id, String name, String headsign, List<StopDTO> stops) {
        this.id = id;
        this.name = name;
        this.headsign = headsign;
        this.stops = stops;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public List<StopDTO> getStops() {
        return stops;
    }

    public void setStops(List<StopDTO> stops) {
        this.stops = stops;
    }
}