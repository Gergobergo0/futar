package futar.futar.service;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.DepartureDTO;
import futar.futar.model.StopDTO;
import org.openapitools.client.api.DefaultApi;

import java.util.*;
import java.util.stream.Collectors;

public class RoutePlannerService {

    private final StopService stopService = new StopService();
    private final DepartureService departureService = new DepartureService(new DefaultApi(ApiClientProvider.getClient()));

    public Optional<RoutePath> findBestRoute(String fromStopName, String toStopName) {
        StopDTO from = stopService.getStopByName(fromStopName);
        StopDTO to = stopService.getStopByName(toStopName);

        if (from == null || to == null) return Optional.empty();

        List<DepartureDTO> departures = departureService.getDepartures(from.getId());

        for (DepartureDTO dep : departures) {
            List<StopDTO> route = stopService.getStopsByTripId(dep.getTripId());

            int fromIndex = findStopIndex(route, from);
            int toIndex = findStopIndex(route, to);


            if (fromIndex >= 0 && toIndex > fromIndex) {
                List<StopDTO> segment = route.subList(fromIndex, toIndex + 1);
                String depTime = dep.getFormattedTime(); // már formázott pl. "14:09"
                String arrTime = departureService.getArrivalTimeAtStop(dep.getTripId(), segment.get(segment.size() - 1).getId()).orElse("?");
                return Optional.of(new RoutePath(dep.getTripId(), segment, depTime, arrTime));
            }
        }

        return Optional.empty(); // nincs közvetlen út
    }

    public List<RoutePath> findTopDirectRoutes(String fromStopName, String toStopName, int limit) {
        double radiusMeters = 100.0;

        List<StopDTO> fromStops = stopService.getNearbyStopsIncludingCenter(fromStopName, radiusMeters);
        List<StopDTO> toStops = stopService.getNearbyStopsIncludingCenter(toStopName, radiusMeters);

        Map<String, RoutePath> routeMap = new HashMap<>(); // kulcs: tripId

        for (StopDTO from : fromStops) {
            List<DepartureDTO> departures = departureService.getDepartures(from.getId());

            for (DepartureDTO dep : departures) {
                List<StopDTO> route = stopService.getStopsByTripId(dep.getTripId());

                for (StopDTO to : toStops) {
                    int fromIndex = findStopIndex(route, from);
                    int toIndex = findStopIndex(route, to);

                    if (fromIndex >= 0 && toIndex > fromIndex) {
                        List<StopDTO> segment = route.subList(fromIndex, toIndex + 1);
                        String arrTime = departureService.getArrivalTimeAtStop(dep.getTripId(), to.getId()).orElse("?");

                        // csak akkor rakjuk be, ha még nincs benne ez a tripId
                        routeMap.putIfAbsent(dep.getTripId(), new RoutePath(dep.getTripId(), segment, dep.getFormattedTime(), arrTime));
                    }
                }
            }
        }

        return routeMap.values().stream()
                .sorted(Comparator.comparingInt(r -> r.stops().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }


    private int findStopIndex(List<StopDTO> route, StopDTO target) {
        for (int i = 0; i < route.size(); i++) {
            StopDTO stop = route.get(i);
            // ID vagy név alapján történő egyezés
            if (stop.getId().equals(target.getId()) || stop.getName().equalsIgnoreCase(target.getName())) {
                return i;
            }
        }
        return -1;
    }


    public void printDirectRoutesPretty(List<RoutePath> routes) {
        System.out.println("⚠️ route count = " + routes.size());

        for (RoutePath routePath : routes) {
            List<StopDTO> route = routePath.stops();
            if (route.isEmpty()) continue;

            StopDTO start = route.get(0);
            StopDTO end = route.get(route.size() - 1);

            String tripId = routePath.tripId();
            String routeName = departureService.getRouteNameByTripId(tripId).orElse("Ismeretlen járat");

            System.out.println("\n🚌 " + routeName);
            System.out.println("Indulás: " + start.getName() + " [" + start.getId() + "] – " + routePath.departureTime());
            System.out.println("Érkezés: " + end.getName() + " [" + end.getId() + "] – " + routePath.arrivalTime());
            System.out.println("\nMegállók:");
            for (StopDTO stop : route) {
                System.out.println(" - " + stop.getName());
            }
            System.out.println("\nLeszállás: " + end.getName());
            System.out.println("──────────────────────────────────────────────");
        }
    }

    public record RoutePath(String tripId, List<StopDTO> stops, String departureTime, String arrivalTime) {
    }


    // Debughoz használható:
    public void printNearbyStops(String stopName) {
        List<StopDTO> nearby = stopService.getNearbyStopsIncludingCenter(stopName, 150);
        System.out.println("🔎 Megállók „" + stopName + "” környékén:");
        for (StopDTO s : nearby) {
            System.out.println(" - " + s.getName() + " [" + s.getId() + "]");
        }
    }
}