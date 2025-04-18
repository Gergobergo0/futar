package futar.futar.context;

import futar.futar.service.GtfsDataService;

public class ApplicationContext {
    private static final GtfsDataService gtfsDataService = new GtfsDataService();

    static {
        // App induláskor GTFS inicializálás
        gtfsDataService.initialize();
    }

    public static GtfsDataService gtfs() {
        return gtfsDataService;
    }
}