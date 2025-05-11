package futar.futar.model;

/**
 * Kedvenc útvonal objektum
 */
public class FavoriteRoute {
    private String name;
    private String fromStop;
    private String toStop;

    /**
     * Létrehoz egy {@code FavoriteRoute} objektumot
     *
     * @param name a kedvenc útvonal megjelenítendő neve
     * @param fromStop a kezdő megálló azonosítója vagy neve
     * @param toStop a cél megálló azonosítója vagy neve
     */
    public FavoriteRoute(String name, String fromStop, String toStop) {
        this.name = name;
        this.fromStop = fromStop;
        this.toStop = toStop;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromStop() {
        return fromStop;
    }

    public void setFromStop(String fromStop) {
        this.fromStop = fromStop;
    }

    public String getToStop() {
        return toStop;
    }

    public void setToStop(String toStop) {
        this.toStop = toStop;
    }
}
