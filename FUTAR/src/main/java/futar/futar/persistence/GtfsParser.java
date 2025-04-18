package futar.futar.persistence;

import futar.futar.model.gtfs.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;

public class GtfsParser {
    private final String basePath;

    public GtfsParser(String basePath) {
        this.basePath = basePath;
    }



    public List<GtfsStopDTO> parseStops() throws Exception {
        List<GtfsStopDTO> stops = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(basePath, "stops.txt").toFile()))) {
            String[] header = reader.readLine().split(",");
            Map<String, Integer> col = indexMap(header);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = parseCsvLine(line);
                String stopId = fields[col.get("stop_id")];
                String stopName = fields[col.get("stop_name")];
                double stopLat = fields[col.get("stop_lat")].isEmpty() ? 0.0 : Double.parseDouble(fields[col.get("stop_lat")]);
                double stopLon = fields[col.get("stop_lon")].isEmpty() ? 0.0 : Double.parseDouble(fields[col.get("stop_lon")]);
                String stopCode = col.containsKey("stop_code") ? fields[col.get("stop_code")] : null;
                String stopDesc = col.containsKey("stop_desc") ? fields[col.get("stop_desc")] : null;
                String zoneId = col.containsKey("zone_id") ? fields[col.get("zone_id")] : null;
                String stopUrl = col.containsKey("stop_url") ? fields[col.get("stop_url")] : null;
                int locationType = 0;
                if (col.containsKey("location_type")) {
                    Integer idx = col.get("location_type");
                    if (idx != null && idx < fields.length && !fields[idx].isEmpty()) {
                        locationType = Integer.parseInt(fields[idx]);
                    }
                }
                String parentStation = col.containsKey("parent_station") ? fields[col.get("parent_station")] : null;

                GtfsStopDTO s = new GtfsStopDTO(stopId, stopCode, stopName, stopDesc, stopLat, stopLon, zoneId, stopUrl, locationType, parentStation);
                stops.add(s);
            }
        }
        return stops;
    }

    public List<GtfsCalendarDateDTO> parseCalendarDates() throws Exception {
        List<GtfsCalendarDateDTO> dates = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(basePath, "calendar_dates.txt").toFile()))) {
            String[] header = reader.readLine().split(",");
            Map<String, Integer> col = indexMap(header);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = parseCsvLine(line);
                GtfsCalendarDateDTO dto = new GtfsCalendarDateDTO(
                        fields[col.get("service_id")],
                        fields[col.get("date")],
                        Integer.parseInt(fields[col.get("exception_type")])
                );
                dates.add(dto);
            }
        }
        return dates;
    }

    private Map<String, Integer> indexMap(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            map.put(header[i].trim().replaceAll("\"", ""), i);
        }
        return map;
    }

    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

// GTFS Parser – trips.txt beolvasása (persistence/GtfsParser.java – folytatás)

public List<GtfsTripDTO> parseTrips() throws Exception {
    List<GtfsTripDTO> trips = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(basePath, "trips.txt").toFile()))) {
        String[] header = reader.readLine().split(",");
        Map<String, Integer> col = indexMap(header);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = parseCsvLine(line);
            GtfsTripDTO trip = new GtfsTripDTO(
                    fields[col.get("route_id")],
                    fields[col.get("service_id")],
                    fields[col.get("trip_id")],
                    col.containsKey("trip_headsign") ? fields[col.get("trip_headsign")] : null,
                    col.containsKey("direction_id") ? fields[col.get("direction_id")] : null
            );
            trips.add(trip);
        }
    }
    return trips;
}

// GTFS Parser – stop_times.txt beolvasása
public List<GtfsStopTimeDTO> parseStopTimes() throws Exception {
    List<GtfsStopTimeDTO> stopTimes = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(basePath, "stop_times.txt").toFile()))) {
        String[] header = reader.readLine().split(",");
        Map<String, Integer> col = indexMap(header);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = parseCsvLine(line);
            GtfsStopTimeDTO st = new GtfsStopTimeDTO(
                    fields[col.get("trip_id")],
                    fields[col.get("arrival_time")],
                    fields[col.get("departure_time")],
                    fields[col.get("stop_id")],
                    Integer.parseInt(fields[col.get("stop_sequence")])
            );
            stopTimes.add(st);
        }
    }
    return stopTimes;
}


    public List<GtfsRouteDTO> parseRoutes() throws Exception {
        List<GtfsRouteDTO> routes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(basePath, "routes.txt").toFile()))) {
            String[] header = reader.readLine().split(",");
            Map<String, Integer> col = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                col.put(header[i], i);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = parseCsvLine(line);
                String routeId = fields[col.get("route_id")];
                String agencyId = fields[col.get("agency_id")];
                String routeShortName = fields[col.get("route_short_name")];
                String routeLongName = fields[col.get("route_long_name")];
                String routeType = fields[col.get("route_type")];
                routes.add(new GtfsRouteDTO(routeId, agencyId, routeShortName, routeLongName, routeType));
            }
        }
        return routes;
    }



}