package futar.futar.service;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.PathStep;
import futar.futar.model.TransitRoute;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.time.*;
import java.util.List;

public class RoutePlannerService {

    private final DefaultApi api;

    public RoutePlannerService() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    public TransitRoute planRoute(String fromName, double fromLat, double fromLon,
                                  String toName, double toLat, double toLon,
                                  String time, String date,
                                  String mode, boolean arriveBy) throws Exception {

        String fromPlace = String.format("%s::%f,%f", fromName, fromLat, fromLon);
        String toPlace = String.format("%s::%f,%f", toName, toLat, toLon);

        List<TraverseMode> modes = switch (mode.toUpperCase()) {
            case "WALK" -> List.of(TraverseMode.WALK);
            case "BICYCLE" -> List.of(TraverseMode.BICYCLE);
            case "TRANSIT" -> List.of(TraverseMode.TRANSIT);
            case "TRANSIT,WALK", "WALK,TRANSIT" -> List.of(TraverseMode.TRANSIT, TraverseMode.WALK);
            default -> throw new IllegalArgumentException("Ismeretlen mód: " + mode);
        };

        // ✅ Epoch millis számítása
        long dateTimeMillis = LocalDate.parse(date)
                .atTime(LocalTime.parse(time))
                .atZone(ZoneId.of("Europe/Budapest"))
                .toInstant()
                .toEpochMilli();

        PlanTripResponse response = api.planTrip(
                Dialect.OTP,
                fromPlace,
                toPlace,
                modes,                // ✅ List<TraverseMode>
                null,                 // ApiVersion (nem kell megadni)
                null,                 // appVersion
                null,                 // includeReferences
                date,                 // pl. "2025-05-05"
                time,                 // pl. "19:58"
                null,                 // shouldBuyTickets
                true,                 // showIntermediateStops
                arriveBy,             // boolean
                null,                 // wheelchair
                null, null, null,     // triangle factors
                null,                 // optimize
                null,                 // walkProfile
                null                  // numItineraries
        );



        TripPlan plan = response.getData().getEntry().getPlan();
        if (plan == null || plan.getItineraries() == null || plan.getItineraries().isEmpty()) {
            return null;
        }

        Itinerary itinerary = plan.getItineraries().get(0);
        TransitRoute route = new TransitRoute();

        for (Leg leg : itinerary.getLegs()) {
            String from = leg.getFrom().getName();
            String to = leg.getTo().getName();
            String modeStr = leg.getMode().toString();

            String departure = formatAnyTime(leg.getStartTime());
            String arrival = formatAnyTime(leg.getEndTime());
            String routeId = leg.getRouteId();
            String tripId = leg.getTripId();
            String routeName = leg.getRouteShortName();
            String label = "[" + modeStr + "] " + (routeName != null ? routeName : (tripId != null ? tripId : "N/A"));
            String stopId = leg.getFrom().getStopId();

            PathStep step = new PathStep(label, from, to, departure, arrival, tripId, modeStr, stopId);
            route.addStep(step);
        }

        route.setStartTime(parseToLocalTime(itinerary.getStartTime()));
        route.setArrivalTime(parseToLocalTime(itinerary.getEndTime()));

        return route;
    }


    // ─────────────────────────────────────────────
    // ÚJ időformázók millis alapú mezőkhöz
    // ─────────────────────────────────────────────

    private String formatTimeFromMillis(Long millis) {
        if (millis == null) return null;
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime().toString();
    }

    private LocalTime formatTimeToLocalTime(OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalTime();
    }

    private String formatTimeFromOffsetDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalTime().toString();
    }

    private String formatAnyTime(Object time) {
        try {
            if (time instanceof OffsetDateTime odt) {
                return odt.toLocalTime().toString();
            } else if (time instanceof Long millis) {
                return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime().toString();
            } else {
                // Esetleg stringből próbáljunk longot olvasni
                String raw = time.toString();
                if (raw.matches("\\d{13}")) {
                    long millis = Long.parseLong(raw);
                    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime().toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Nem sikerült időt értelmezni: " + time + " → " + e.getMessage());
        }
        return null;
    }

    private LocalTime parseToLocalTime(Object time) {
        try {
            if (time instanceof OffsetDateTime odt) {
                return odt.toLocalTime();
            } else if (time instanceof Long millis) {
                return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime();
            } else {
                String raw = time.toString();
                if (raw.matches("\\d{13}")) {
                    long millis = Long.parseLong(raw);
                    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime();
                }
            }
        } catch (Exception e) {
            System.err.println("Nem sikerült időt konvertálni LocalTime-ra: " + time + " → " + e.getMessage());
        }
        return null;
    }


}
