package futar.futar.model;

public class PathStep {
    private String label;
    private String from;
    private String to;
    private String departure;
    private String arrival;
    private String tripId; // új mező
    private String mode;
    private String stopId;

    public PathStep(String label, String from, String to, String departure, String arrival, String tripId, String mode, String stopId) {
        this.label = label;
        this.from = from;
        this.to = to;
        this.departure = departure;
        this.arrival = arrival;
        this.tripId = tripId;
        this.mode = mode;
    }

    public String getStopId(){return stopId;}

    // Getterek és setterek (ha kell)

    public String getMode(){return mode;}
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public String toString() {
        return label + ": " + from + " → " + to + " (" + departure + " → " + arrival + ")";
    }

    public String getTripId() {
        return tripId;
    }
}
