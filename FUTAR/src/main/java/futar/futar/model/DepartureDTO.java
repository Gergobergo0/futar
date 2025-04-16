package futar.futar.model;

public class DepartureDTO {
    private final String route;
    private final String headsign;
    private final String formattedTime;
    private final String tripHeadsign;
    private final long minutes;

    public DepartureDTO(String route, String headsign, String formattedTime, String tripHeadsign, long minutes) {
        this.route = route;
        this.headsign = headsign;
        this.formattedTime = formattedTime;
        this.tripHeadsign = tripHeadsign;
        this.minutes = minutes;
    }
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
