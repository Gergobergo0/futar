package futar.futar.model;

/**
 * Kedvenc megálló elmentéshez szükséges osztály
 */
public class FavoriteStop {

    private String name;
    private String stopId;
    private String stopName;

    /**
     * létrehoz egy kedvenc megállót
     * @param name megálló custom neve
     * @param stopId megálló id
     * @param stopName meglló név
     */
    public FavoriteStop(String name, String stopId, String stopName) {
        this.name = name;
        this.stopId = stopId;
        this.stopName = stopName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getName() {
        return name;
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }
}
