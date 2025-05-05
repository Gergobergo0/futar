package futar.futar.service;

import futar.futar.api.ApiClientProvider;
import futar.futar.model.PathStep;
import org.openapitools.client.api.DefaultApi;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RouteValidatorService {

    private final DepartureService departureService;
    private final StopService stopService;

    public RouteValidatorService() {
        this.departureService = new DepartureService(new DefaultApi(ApiClientProvider.getClient()));
        this.stopService = new StopService();
    }

    public boolean validatePlannedRoute(List<PathStep> steps) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (PathStep step : steps) {
            String mode = step.getMode();
            String tripId = step.getTripId();

            // Csak érvényes, közlekedési lépést validálunk
            if (mode == null || mode.equalsIgnoreCase("WALK") || tripId == null || tripId.isBlank() || tripId.equalsIgnoreCase("walk")) {
                continue;
            }

            // Stop ID lekérése a megálló nevéből
            String stopId = stopService.getStopIdByName(step.getFrom());
            if (stopId == null) {
                System.out.println("⚠️  Nem található stopId a megállóhoz: " + step.getFrom());
                continue;
            }

            // Valós idejű érkezési idő lekérése
            Optional<String> predictedDepartureOpt = departureService.getArrivalTimeAtStop(tripId, stopId);
            if (predictedDepartureOpt.isEmpty()) {
                System.out.println("⚠️  Nincs valós idejű adat elérhető ehhez a lépéshez: " + step);
                continue;
            }

            // Idők összehasonlítása
            String plannedTimeStr = step.getDeparture();
            String predictedTimeStr = predictedDepartureOpt.get();

            try {
                LocalTime plannedTime = LocalTime.parse(plannedTimeStr, formatter);
                LocalTime predictedTime = LocalTime.parse(predictedTimeStr, formatter);

                long diff = Math.abs(predictedTime.toSecondOfDay() - plannedTime.toSecondOfDay()) / 60;

                if (diff > 3) {
                    System.out.println("🚨 Eltérés a menetrendhez képest (" + diff + " perc):");
                    System.out.println("🕒 Tervezett: " + plannedTimeStr + " | Valós: " + predictedTimeStr);
                    System.out.println("🔁 Újratervezés javasolt erre a szakaszra: " + step);
                    return false;
                }

            } catch (Exception e) {
                System.out.println("⛔ Hiba az idő formátum feldolgozásánál: " + e.getMessage());
            }
        }

        System.out.println("✅ Az útvonal menetrend szerint halad.");
        return true;
    }
}
