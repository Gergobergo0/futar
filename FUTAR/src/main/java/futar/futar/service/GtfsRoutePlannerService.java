// GtfsRoutePlannerService (service/GtfsRoutePlannerService.java)
package futar.futar.service;

import futar.futar.context.ApplicationContext;
import futar.futar.model.RouteDTO;
import futar.futar.model.StopDTO;
import futar.futar.model.gtfs.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GtfsRoutePlannerService {

    public record RoutePath(String tripId, String routeId, String headsign, List<GtfsStopDTO> stops, String departureTime, String arrivalTime) {}

    public List<RoutePath> findTopDirectRoutes(String fromStopName, String toStopName, String afterTime, int limit) {
        GtfsDataService gtfs = ApplicationContext.gtfs();
        List<GtfsStopDTO> stops = gtfs.getStops();
        List<GtfsTripDTO> trips = gtfs.getTrips();
        List<GtfsStopTimeDTO> stopTimes = gtfs.getStopTimes();
        List<GtfsRouteDTO> routes = gtfs.getRoutes();

        Map<String, GtfsStopDTO> stopById = stops.stream().collect(Collectors.toMap(GtfsStopDTO::getStopId, s -> s));
        Map<String, GtfsTripDTO> tripById = trips.stream().collect(Collectors.toMap(GtfsTripDTO::getTripId, t -> t));
        Map<String, GtfsRouteDTO> routeById = routes.stream().collect(Collectors.toMap(GtfsRouteDTO::getRouteId, r -> r));

        Set<String> fromStopIds = findNearbyStopIds(fromStopName, stops, 100);
        Set<String> toStopIds = findNearbyStopIds(toStopName, stops, 100);

        Map<String, List<GtfsStopTimeDTO>> stopTimesByTrip = stopTimes.stream()
                .collect(Collectors.groupingBy(GtfsStopTimeDTO::getTripId));

        Set<String> uniqueRoutes = new HashSet<>();
        List<RoutePath> foundRoutes = new ArrayList<>();

        LocalTime after;
        try {
            after = parseTimeSafe(afterTime);
        } catch (DateTimeParseException e) {
            System.err.println("Hibás időformátum: " + afterTime);
            return Collections.emptyList();
        }

        for (GtfsTripDTO trip : trips) {
            List<GtfsStopTimeDTO> tripStopTimes = stopTimesByTrip.get(trip.getTripId());
            if (tripStopTimes == null) continue;

            OptionalInt fromIndexOpt = findStopIndex(tripStopTimes, fromStopIds);
            OptionalInt toIndexOpt = findStopIndex(tripStopTimes, toStopIds);

            if (fromIndexOpt.isEmpty() || toIndexOpt.isEmpty()) continue;

            int fromIndex = fromIndexOpt.getAsInt();
            int toIndex = toIndexOpt.getAsInt();

            if (fromIndex < toIndex) {
                GtfsStopTimeDTO fromTime = tripStopTimes.get(fromIndex);
                GtfsStopTimeDTO toTime = tripStopTimes.get(toIndex);

                LocalTime depTime;
                try {
                    depTime = parseTimeSafe(fromTime.getDepartureTime());
                } catch (DateTimeParseException e) {
                    continue;
                }

                if (depTime.isBefore(after)) continue;

                List<GtfsStopDTO> path = new ArrayList<>();
                for (int i = fromIndex; i <= toIndex; i++) {
                    GtfsStopTimeDTO st = tripStopTimes.get(i);
                    GtfsStopDTO stop = stopById.get(st.getStopId());
                    if (stop != null) path.add(stop);
                }

                String uniqueKey = trip.getRouteId() + "|" + path.get(0).getStopId() + "|" + path.get(path.size() - 1).getStopId();
                if (uniqueRoutes.contains(uniqueKey)) continue;
                uniqueRoutes.add(uniqueKey);

                foundRoutes.add(new RoutePath(trip.getTripId(), trip.getRouteId(), trip.getTripHeadsign(), path, fromTime.getDepartureTime(), toTime.getArrivalTime()));
            }
        }

        return foundRoutes.stream()
                .sorted(Comparator.comparing(r -> parseTimeSafe(r.arrivalTime())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private OptionalInt findStopIndex(List<GtfsStopTimeDTO> tripStops, Set<String> stopIds) {
        for (int i = 0; i < tripStops.size(); i++) {
            if (stopIds.contains(tripStops.get(i).getStopId())) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    private Set<String> findNearbyStopIds(String referenceStopName, List<GtfsStopDTO> allStops, double radiusMeters) {
        List<GtfsStopDTO> referenceStops = allStops.stream()
                .filter(s -> s.getStopName().equalsIgnoreCase(referenceStopName))
                .collect(Collectors.toList());

        Set<String> result = new HashSet<>();

        for (GtfsStopDTO ref : referenceStops) {
            for (GtfsStopDTO s : allStops) {
                if (distanceMeters(ref.getStopLat(), ref.getStopLon(), s.getStopLat(), s.getStopLon()) <= radiusMeters) {
                    result.add(s.getStopId());
                }
            }
        }
        return result;
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private LocalTime parseTimeSafe(String timeStr) {
        String[] parts = timeStr.split(":");
        int hour = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
        int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int second = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        if (hour >= 24) hour = hour % 24;
        return LocalTime.of(hour, minute, second);
    }

    public void printDirectRoutesPretty(List<RoutePath> routes) {
        GtfsDataService gtfs = ApplicationContext.gtfs();
        Map<String, GtfsRouteDTO> routeById = gtfs.getRoutes().stream()
                .collect(Collectors.toMap(GtfsRouteDTO::getRouteId, r -> r));

        for (RoutePath route : routes) {
            if (route.stops().isEmpty()) continue;
            GtfsStopDTO start = route.stops().get(0);
            GtfsStopDTO end = route.stops().get(route.stops().size() - 1);

            String shortName = routeById.containsKey(route.routeId) ? routeById.get(route.routeId).getRouteShortName() : route.routeId;
            String longName = routeById.containsKey(route.routeId) ? routeById.get(route.routeId).getRouteLongName() : "";

            System.out.println("\n🚌 Járat: " + shortName + " – " + longName + " (" + route.headsign + ")");
            System.out.println("Indulás: " + start.getStopName() + " (" + start.getStopId() + ") @ " + route.departureTime());
            System.out.println("Érkezés: " + end.getStopName() + " (" + end.getStopId() + ") @ " + route.arrivalTime());
            System.out.println("Megállók:");
            for (GtfsStopDTO stop : route.stops()) {
                System.out.println(" - " + stop.getStopName() + " [" + stop.getStopId() + "] " + shortName);
            }
        }
    }

    public List<GtfsRouteDTO> parseRoutes(String basePath) throws Exception {
        List<GtfsRouteDTO> routes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(basePath, "routes.txt").toFile()))) {
            String[] header = reader.readLine().split(",");
            Map<String, Integer> col = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                col.put(header[i], i);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
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