package futar.futar.model;

public class StopDTO {
    private String name;
    private double lat;
    private double lon;
    private final String id;

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

    public StopDTO(String name, Double lat, Double lon, String id) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.id = id;

    }
}

