package futar.futar.model;

/**
 * Megállók osztálya
 */
public class StopDTO {
    private String name;
    private double lat;
    private double lon;
    private String id;
    private long arrivalEpochSeconds;

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getId()
    {
        return id;
    }
    /**
     * Létrehoz egy megálló objektumot érkezési idővel.
     *
     * @param name a megálló neve
     * @param lat a megálló szélességi koordinátája
     * @param lon a megálló hosszúsági koordinátája
     * @param id a megálló egyedi azonosítója
     * @param arrivalEpochSeconds érkezési idő unix timestamp formában (mp-ben)
     */
    public StopDTO(String name, Double lat, Double lon, String id, long arrivalEpochSeconds) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.id = id;
        this.arrivalEpochSeconds = arrivalEpochSeconds;
    }

    public long getArrivalEpochSeconds() {
        return arrivalEpochSeconds;
    }

    /**
     * Létrehoz egy megálló objektumot érkezési idő nélkül alapból 0
     *
     * @param name a megálló neve
     * @param lat a megálló szélességi koordinátája
     * @param lon a megálló hosszúsági koordinátája
     * @param id a megálló egyedi azonosítója
     */
    public StopDTO(String name, double lat, double lon, String id) {
        this(name, lat, lon, id, 0);// alapértelmezett érkezési idő = 0
    }


    public void setId(String stopId)
    {
        this.id = stopId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}

