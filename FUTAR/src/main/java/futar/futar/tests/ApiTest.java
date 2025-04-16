package futar.futar.tests;

import org.openapitools.client.*;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import futar.futar.model.StopDTO;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ApiTest {
    private static final String BASE_PATH = "https://futar.bkk.hu/api/query/v1/ws";
    private static final String API_KEY = "38ff1614-fa20-40ee-bc79-5c3f5490603c"; // ideiglenes kulcs

    public static void main(String[] args) {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath(BASE_PATH);
        client.addDefaultHeader("key", API_KEY);

        DefaultApi api = new DefaultApi(client);

        try {
            // 1. Megállók lekérdezése Astoria körül
            StopsForLocationResponse stopsResponse = api.getStopsForLocation(
                    Dialect.OTP,
                    47.4945f,               // lat
                    19.0604f,               // lon
                    300f,                   // radius
                    null,                   // latSpan
                    null,                   // lonSpan
                    null,                   // query
                    null,                   // minResult
                    null,                   // appVersion
                    null,                   // apiVersion
                    null                    // includeReferences
            );


            List<TransitStop> rawStops = stopsResponse.getData().getList();

            List<StopDTO> stops = rawStops.stream()
                    .map(stop -> new StopDTO(
                            stop.getName(),
                            stop.getLat(),
                            stop.getLon(),
                            stop.getId() // Ezt is mentsük, ha kell az indulásokhoz
                    ))
                    .toList();

            String stopId = rawStops.get(0).getId(); // Ezt várja az API


            if (stops.isEmpty()) {
                System.out.println("Nem található megálló a környéken.");
                return;
            }

            System.out.println("Talált megállók:");
            for (int i = 0; i < Math.min(3, stops.size()); i++) {
                System.out.println("- " + stops.get(i).getName() + " | ID: " + stops.get(i).getId());
            }

            // 2. Első megálló ID-jét felhasználjuk

            // 3. Indulások lekérdezése
            ArrivalsAndDeparturesForStopOTPMethodResponse response = api.getArrivalsAndDeparturesForStop(
                    Dialect.OTP,
                    0,                      // minutesBefore
                    30,                     // minutesAfter
                    List.of(stopId),        // stopId lista!
                    null, null,
                    true,                   // onlyDepartures
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

            System.out.println("\nIndulások:");
            for (TransitScheduleStopTime stopTime : stopTimes) {
                String destination = stopTime.getStopHeadsign();
                long departureTime = stopTime.getPredictedDepartureTime() != null
                        ? stopTime.getPredictedDepartureTime()
                        : stopTime.getDepartureTime();

                String timeFormatted = Instant.ofEpochSecond(departureTime)
                        .atZone(ZoneId.of("Europe/Budapest"))
                        .format(DateTimeFormatter.ofPattern("HH:mm"));

                // Járatszám lekérdezése
                String tripId = stopTime.getTripId();
                String routeShortName = "?";
                if (tripId != null && trips.containsKey(tripId)) {
                    TransitTrip trip = trips.get(tripId);
                    String routeId = trip.getRouteId();
                    if (routeId != null && routes.containsKey(routeId)) {
                        routeShortName = routes.get(routeId).getShortName();
                    }
                }

                System.out.println("- " + routeShortName + " → " + destination + " | Indulás: " + timeFormatted);
            }


/*
            List<TransitArrivalsAndDepartures> departures =
                    response.getData().getEntry().getArrivalsAndDepartures();

            System.out.println("\nIndulások:");
            for (TransitArrivalsAndDepartures dep : departures) {
                System.out.println("- " + dep.getRouteShortName() + " → " + dep.getTripHeadsign() +
                        " | " + dep.getScheduledDepartureTime() + " sec");
            }*/



        } catch (Exception e) {
            System.out.println("Hiba történt:");
            e.printStackTrace();
        }
    }
}
