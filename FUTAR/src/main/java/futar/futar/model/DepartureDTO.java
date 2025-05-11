package futar.futar.model;

/**
 * Ez az osztály egy dataTransferObject a járat közelgő indulásának adatait tárolja
 *
 */
public class DepartureDTO {
    private String tripId;
    private final String route;
    private final String headsign;
    private final String formattedTime;
    private final String tripHeadsign;
    private final long minutes;
    private final String type;
    /**
     * Létrehoz egy {@code DepartureDTO} objektumot az adott adatokkal.
     *
     * @param route a járat száma
     * @param headsign a járat iránya, ami ki van írva (pl. "Deák Ferenc Tér M")
     * @param formattedTime az indulási idő formázva (pl. "14:32")
     * @param tripHeadsign a járat belső iránycímke, ami a rendszerben szerepel
     * @param minutes a jelenlegi időponthoz képesti indulásig hátralévő percek
     * @param tripId a járat egyedi azonosítója
     * @param type a járat típusa pl BUS, TRAM stb
     */
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
