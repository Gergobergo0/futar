package futar.futar.service;

import futar.futar.api.TripApi;
import futar.futar.model.DepartureDTO;
import futar.futar.model.StopDTO;
import org.openapitools.client.model.*;
import org.openapitools.client.api.DefaultApi;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DepartureService {
    private final DefaultApi api;

    public DepartureService(DefaultApi api) {
        this.api = api;
    }



    public List<DepartureDTO> getDepartures(String stopId) {
        try {
            var response = api.getArrivalsAndDeparturesForStop(
                    Dialect.OTP,
                    0,
                    30,
                    List.of(stopId),
                    null, null,
                    true,
                    10,
                    null, null, null, null, null,
                    null, null,
                    null,
                    null,
                    null
            );

            List<TransitScheduleStopTime> stopTimes = response.getData().getEntry().getStopTimes();
            Map<String, TransitTrip> trips = response.getData().getReferences().getOTPTransitReferences().getTrips();
            Map<String, TransitRoute> routes = response.getData().getReferences().getOTPTransitReferences().getRoutes();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of("Europe/Budapest"));

            return stopTimes.stream()
                    .map(stopTime -> {
                        String tripId = stopTime.getTripId();
                        String routeShortName = "?";
                        String tripHeadsign = "?";

                        if (tripId != null && trips.containsKey(tripId)) {
                            TransitTrip trip = trips.get(tripId);
                            tripHeadsign = trip.getTripHeadsign(); // VÉGÁLLOMÁS

                            String routeId = trip.getRouteId();
                            if (routeId != null && routes.containsKey(routeId)) {
                                routeShortName = routes.get(routeId).getShortName();
                            }
                        }

                        String headsign = stopTime.getStopHeadsign();
                        long time = stopTime.getPredictedDepartureTime() != null
                                ? stopTime.getPredictedDepartureTime()
                                : stopTime.getDepartureTime();

                        String formattedTime = formatter.format(Instant.ofEpochSecond(time));
                        long now = Instant.now().getEpochSecond();
                        long minutes = (time - now) / 60;
                        if (minutes < 0) minutes = 0;

                        return new DepartureDTO(routeShortName, headsign, formattedTime, tripHeadsign, minutes, tripId);
                    })
                    .collect(Collectors.toList());


        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<DepartureDTO> getNearbyDepartures(double lat, double lon) {
        try {
            var response = api.getArrivalsAndDeparturesForLocation(
                    Dialect.OTP,
                    null,
                    lon,
                    lat,
                    10,
                    30,
                    null,
                    null,
                    null,
                    true,
                    50,
                    (float) lat,
                    (float) lon,
                    null,
                    null,
                    100,
                    null,
                    null,
                    null,
                    null,null
            );

            List<TransitDepartureGroup> departureGroups = response.getData().getList();
            Map<String, TransitTrip> trips = response.getData().getReferences().getOTPTransitReferences().getTrips();
            Map<String, TransitRoute> routes = response.getData().getReferences().getOTPTransitReferences().getRoutes();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of("Europe/Budapest"));

            return departureGroups.stream()
                    .flatMap(group -> group.getStopTimes().stream())
                    .map(stopTime -> {
                        String tripId = stopTime.getTripId();
                        String routeShortName = "?";
                        String tripHeadsign = "?";

                        if (tripId != null && trips.containsKey(tripId)) {
                            TransitTrip trip = trips.get(tripId);
                            tripHeadsign = trip.getTripHeadsign();
                            String routeId = trip.getRouteId();
                            if (routeId != null && routes.containsKey(routeId)) {
                                routeShortName = routes.get(routeId).getShortName();
                            }
                        }

                        long time = stopTime.getPredictedDepartureTime() != null
                                ? stopTime.getPredictedDepartureTime()
                                : stopTime.getDepartureTime();

                        String formattedTime = formatter.format(Instant.ofEpochSecond(time));
                        long now = Instant.now().getEpochSecond();
                        long minutes = (time - now) / 60;
                        if (minutes < 0) minutes = 0;

                        return new DepartureDTO(routeShortName, tripHeadsign, formattedTime, tripHeadsign, minutes, tripId);
                    })
                    .collect(Collectors.toList());


        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    public Optional<String> getRouteNameByTripId(String tripId) {
        try {

            System.out.println("TRIP ID==" + tripId);
            TripDetailsOTPMethodResponse response = api.getTripDetails(
                    Dialect.OTP, null, tripId, null, null, null, "1.0", null, null
            );

            OTPTransitReferences refs = response.getData().getReferences().getOTPTransitReferences();
            Map<String, TransitTrip> tripMap = refs.getTrips();
            Map<String, TransitRoute> routeMap = refs.getRoutes();

            // 👉 Megkeressük a Trip-et a tripId alapján
            TransitTrip trip = tripMap.get(tripId);
            if (trip == null) return Optional.empty();

            String routeId = trip.getRouteId(); // ✅ innen jön a route ID

            TransitRoute route = routeMap.get(routeId);
            if (route == null) return Optional.empty();

            // 👉 Próbáljuk többféle név alapján
            String name = route.getShortName();
            if (name == null || name.isBlank()) name = route.getLongName();

            return Optional.ofNullable(name);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
    public List<StopDTO> getStopsByTripId(String tripId) {
        try {
            TripApi tripApi = new TripApi();
            return tripApi.getStopsByTrip(tripId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Optional<String> getArrivalTimeAtStop(String tripId, String stopId) {
        try {
            TripDetailsOTPMethodResponse response = api.getTripDetails(
                    Dialect.OTP, null, tripId, null, null, null, "1.0", null, null
            );

            List<TransitTripStopTime> stops = response.getData().getEntry().getStopTimes();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of("Europe/Budapest"));

            for (TransitTripStopTime stop : stops) {
                if (stop.getStopId().equals(stopId)) {
                    long time = stop.getPredictedArrivalTime() != null
                            ? stop.getPredictedArrivalTime()
                            : stop.getArrivalTime();
                    return Optional.of(formatter.format(Instant.ofEpochSecond(time)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    public List<DepartureDTO> getDeparturesForStop(String stopId, double lat, double lon) {
        List<DepartureDTO> precise = getDepartures(stopId);
        if (precise != null && !precise.isEmpty()) return precise;

        // Ha nincs eredmény, próbáljuk GPS alapján lekérni
        return getNearbyDepartures(lat, lon);
    }








}