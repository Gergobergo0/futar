package futar.futar.service;

import futar.futar.model.DepartureDTO;
import org.openapitools.client.model.ArrivalsAndDeparturesForLocationOTPMethodResponse;
import org.openapitools.client.model.ArrivalsAndDeparturesForStopOTPMethodResponse;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Dialect;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.openapitools.client.model.TransitScheduleStopTime;
import org.openapitools.client.model.TransitTrip;
import org.openapitools.client.model.TransitRoute;

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

                        return new DepartureDTO(routeShortName, headsign, formattedTime, tripHeadsign, minutes);
                    })
                    .collect(Collectors.toList());


        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
