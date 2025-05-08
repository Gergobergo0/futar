package futar.futar.api;

import futar.futar.utils.UIUtils;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import org.openapitools.client.model.Dialect;
import org.openapitools.client.model.TripDetailsOTPMethodResponse;
/**
 * Api kérés az indulási és járatinformációk lekérdezéséhez
 *
 * <p>
 * Ez az osztály "becsomagolja" az {@link DefaultApi} hívásokat, és kivétel esetén
 * hibaüzenetet jelenít meg a felhasználónak
 */
public class DepartureApi {
    /**
     * API végpontokat kezelő kliens
     */
    private final DefaultApi api;

    /**
     * Konstruktor létrehozza a az Api klienst
     */
    public DepartureApi() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    /**
     * lekérdezi egy adott megálló érkezéseit és indulásait
     * @param stopId
     * @return {@code null}, ha hiba történt a lekérdezés során (+ felhasználónak értesítés erről)
     *          {@link ArrivalsAndDeparturesForStopOTPMethodResponse} objektum, ha sikeres
     *      */

    public ArrivalsAndDeparturesForStopOTPMethodResponse getArrivalsAndDeparturesForStop(String stopId) {
        try {
            return api.getArrivalsAndDeparturesForStop(
                    Dialect.OTP,
                    0,
                    30,
                    java.util.List.of(stopId),
                    null, null,
                    true,
                    10,
                    null, null, null, null, null,
                    null, null,
                    null,
                    null,
                    null
            );
        } catch (Exception e) {
            UIUtils.showConnectionDialog();
            return null;
        }

    }


    /**
     * Lekérdezi egy járat részletes adatait
     * @param tripId járat azonosítója
     * @return {@link TripDetailsOTPMethodResponse} ha sikeres
     *          {@code null}, ha hiba történt a lekérdezés során (+ felhasználónak értesítés erről)
     */
    public TripDetailsOTPMethodResponse getTripDetails(String tripId) {
        try {
            return api.getTripDetails(
                    Dialect.OTP, null, tripId, null, null, null, "1.0", null, null
            );
        } catch (Exception e)
        {
            UIUtils.showConnectionDialog();
            return null;
        }

    }
}
