package futar.futar.service;

import futar.futar.api.DepartureApi;
import futar.futar.model.DepartureDTO;
import org.openapitools.client.model.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
/**
 * A DepartureService osztály felelős az adott megállóhoz vagy járathoz tartozó
 * indulási és érkezési információk lekéréséért és feldolgozásáért.
 */

public class DepartureService {

    private final DepartureApi departureApi;
    /**
     * Konstruktor, amely inicializálja a DepartureApi példányt.
     */

    public DepartureService() {
        this.departureApi = new DepartureApi();
    }
    /**
     * Lekéri az adott megállóhoz tartozó következő indulásokat, és
     * {@link DepartureDTO} objektumokká alakítja azokat.
     *
     * @param stopId a megálló azonosítója
     * @return indulások listája DTO formában
     */

    public List<DepartureDTO> getDepartures(String stopId) {
        try {
            var response = departureApi.getArrivalsAndDeparturesForStop(stopId);
            List<TransitScheduleStopTime> stopTimes = response.getData().getEntry().getStopTimes();
            Map<String, TransitTrip> trips = response.getData().getReferences().getOTPTransitReferences().getTrips();
            Map<String, TransitRoute> routes = response.getData().getReferences().getOTPTransitReferences().getRoutes();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of("Europe/Budapest"));

            return stopTimes.stream().map(stopTime -> {
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

                long time = Optional.ofNullable(stopTime.getPredictedDepartureTime())
                        .orElse(stopTime.getDepartureTime());

                String formattedTime = formatter.format(Instant.ofEpochSecond(time));
                long now = Instant.now().getEpochSecond();
                long minutes = Math.max((time - now) / 60, 0);

                return new DepartureDTO(routeShortName, stopTime.getStopHeadsign(), formattedTime, tripHeadsign, minutes, tripId);
            }).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


    /**
     * Visszaadja egy adott trip azonosítóhoz tartozó járat nevét (rövid vagy hosszú).
     *
     * @param tripId a járat azonosítója
     * @return a járat neve, ha megtalálható
     */

    public Optional<String> getRouteNameByTripId(String tripId) {
        try {
            var response = departureApi.getTripDetails(tripId);
            OTPTransitReferences refs = response.getData().getReferences().getOTPTransitReferences();
            TransitTrip trip = refs.getTrips().get(tripId);
            if (trip == null) return Optional.empty();
            TransitRoute route = refs.getRoutes().get(trip.getRouteId());
            if (route == null) return Optional.empty();
            String name = Optional.ofNullable(route.getShortName()).orElse(route.getLongName());
            return Optional.ofNullable(name);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }




}
