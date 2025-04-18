package futar.futar.service;

import futar.futar.model.gtfs.*;
import futar.futar.persistence.GtfsParser;

import java.util.List;

public class GtfsDataService {
    private List<GtfsStopDTO> stops;
    private List<GtfsTripDTO> trips;
    private List<GtfsStopTimeDTO> stopTimes;
    private List<GtfsCalendarDateDTO> calendars;
    private List<GtfsRouteDTO> routes;

    public void initialize() {
        try {
            /*
            if (GtfsDownloader.isUpdated()) {
                GtfsExtractor.cleanDirectory("data/gtfs");
                GtfsExtractor.extractGtfs("data/gtfs.zip", "data/gtfs");
            }*/

            GtfsParser parser = new GtfsParser("data/gtfs");
            stops = parser.parseStops();
            trips = parser.parseTrips();
            stopTimes = parser.parseStopTimes();
            calendars = parser.parseCalendarDates();
            routes = parser.parseRoutes();

            System.out.println("✅ GTFS adatok betöltve:");
            System.out.println("• Megállók száma: " + stops.size());
            System.out.println("• Járatok száma: " + trips.size());
            System.out.println("• Megállási időpontok száma: " + stopTimes.size());
            System.out.println("• Naptár bejegyzések száma: " + calendars.size());
        } catch (Exception e) {
            System.err.println("❌ Hiba a GTFS inicializálás során:");
            e.printStackTrace();
        }
    }
    public List<GtfsRouteDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GtfsRouteDTO> routes) {
        this.routes = routes;
    }
    public List<GtfsStopDTO> getStops() {
        return stops;
    }

    public List<GtfsTripDTO> getTrips() {
        return trips;
    }

    public List<GtfsStopTimeDTO> getStopTimes() {
        return stopTimes;
    }

    public List<GtfsCalendarDateDTO> getCalendars() {
        return calendars;
    }
}
