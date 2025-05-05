package futar.futar.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StopTimeDTO {
    private String stopId;
    private String stopName;
    private double lat;
    private double lon;
    private long arrivalTime;
    private long departureTime;
    private String tripId;
    private String routeName;

    public StopTimeDTO(String stopId, String stopName, double lat, double lon, long arrivalTime, long departureTime, String tripId, String routeName) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.lat = lat;
        this.lon = lon;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.tripId = tripId;
        this.routeName = routeName;
    }

    public StopTimeDTO(String stopId, String stopName, long arrivalTime) {
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
    public String getFormattedTime() {
        if (arrivalTime <= 0) return "N/A";
        return DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.of("Europe/Budapest"))
                .format(Instant.ofEpochSecond(arrivalTime));
    }
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
