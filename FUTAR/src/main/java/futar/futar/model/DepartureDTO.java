package futar.futar.model;

public class DepartureDTO {
    private String tripId;
    private final String route;
    private final String headsign;
    private final String formattedTime;
    private final String tripHeadsign;
    private final long minutes;
    private final String type;

    public DepartureDTO(String route, String headsign, String formattedTime, String tripHeadsign, long minutes, String tripId, String type) {
        this.route = route;
        this.headsign = headsign;
        this.formattedTime = formattedTime;
        this.tripHeadsign = tripHeadsign;
        this.minutes = minutes;
        this.tripId = tripId;
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public String getTripId(){return tripId;}
    public String getTripHeadsign() {return tripHeadsign;}
    public String getRoute() {
        return route;
    }

    public String getHeadsign() {
        return headsign;
    }

    public String getFormattedTime() {
        return formattedTime;
    }
    public long getMinutes() {
        return minutes;
    }
}
