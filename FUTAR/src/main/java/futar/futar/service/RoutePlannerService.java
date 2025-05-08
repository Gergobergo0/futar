package futar.futar.service;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.PathStep;
import futar.futar.model.TransitRoute;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.time.*;
import java.util.List;
/**
 * A RoutePlannerService felelős az útvonaltervezési kérelmek elküldéséért az OpenAPI felé,
 * és a válasz feldolgozásáért egy TransitRoute objektummá.
 */

public class RoutePlannerService {

    private final DefaultApi api;
    /**
     * Konstruktor, amely inicializálja az API klienst.
     */

    public RoutePlannerService() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }
    /**
     * Útvonalat tervez két földrajzi pozíció és név között megadott időpont és közlekedési mód alapján.
     *
     * @param fromName  indulási hely neve
     * @param fromLat   indulási hely szélességi koordinátája
     * @param fromLon   indulási hely hosszúsági koordinátája
     * @param toName    célhely neve
     * @param toLat     célhely szélességi koordinátája
     * @param toLon     célhely hosszúsági koordinátája
     * @param time      időpont (HH:mm)
     * @param date      dátum (yyyy-MM-dd)
     * @param mode      közlekedési mód (pl. "TRANSIT,WALK")
     * @param arriveBy  ha true, az érkezési időt veszi figyelembe; ha false, az indulásit
     * @return egy {@link TransitRoute} objektum az útvonal részleteivel, vagy null ha nem található
     * @throws Exception hiba esetén kivételt dob
     */

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

        // epoch millis
        long dateTimeMillis = LocalDate.parse(date)
                .atTime(LocalTime.parse(time))
                .atZone(ZoneId.of("Europe/Budapest"))
                .toInstant()
                .toEpochMilli();

        PlanTripResponse response = api.planTrip(
                Dialect.OTP,
                fromPlace,
                toPlace,
                modes,
                null,
                null,
                null,
                date,
                time,
                null,
                true,
                arriveBy,
                null,
                null, null, null,
                null,
                null,
                null
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


    /**
     * Általános időformátum-értelmezés különböző típusokra (OffsetDateTime, Long, String millis).
     *
     * @param time az idő objektum (OffsetDateTime, Long vagy String millis)
     * @return a formázott idő szövegként, vagy null ha nem sikerült
     */

    private String formatAnyTime(Object time) {
        try {
            if (time instanceof OffsetDateTime odt) {
                return odt.toLocalTime().toString();
            } else if (time instanceof Long millis) {
                return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime().toString();
            } else {
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
    /**
     * Átalakítja az időt {@link LocalTime}-ra különböző formátumokból.
     *
     * @param time az idő objektum (OffsetDateTime, Long vagy String millis)
     * @return a {@link LocalTime} objektum, vagy null ha nem sikerült
     */

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
