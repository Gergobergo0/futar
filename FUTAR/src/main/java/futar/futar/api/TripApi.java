package futar.futar.api;

import futar.futar.model.StopDTO;
import futar.futar.utils.UIUtils;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * API kérés ami a megadott tripId-hoz kéri le a megállókat
 * <p>
 *      Az osztály feldolgozza a válaszban érkező megálló és időadatokat, majd azokat saját DTO-ként adja vissza
 * </p>
 */
public class TripApi {
    /**
     * API végpontot kezelő kliens
     */
    private final DefaultApi api;
    /**
     * Konstruktor, létrehoz egy új api klienst
     */
    public TripApi() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    /**
     * Az adott megállókat lekérdezése és átalakítása {@link StopDTO} objektummá
     * @param tripId járat azonosítója
     * @return megállók listája vagy {@code null} ha hiba történt
     * @throws Exception ha hiba történik
     */
    public List<StopDTO> getStopsByTrip(String tripId) throws Exception {
        try {
            TripDetailsOTPMethodResponse response = api.getTripDetails(
                    Dialect.OTP,
                    null,
                    tripId,
                    null,
                    null,
                    null,
                    "1.0",
                    null,
                    null
            );
            //megállók és időadatok kibontása
            List<TransitTripStopTime> stopTimes = response.getData().getEntry().getStopTimes();
            OTPTransitReferences otpRefs = response.getData().getReferences().getOTPTransitReferences();
            Map<String, TransitStop> stopMap = otpRefs.getStops();
            //átalakkítás StopDTO-ra
            List<StopDTO> result = new ArrayList<>();
            for (TransitTripStopTime stopTime : stopTimes) {
                String stopId = stopTime.getStopId();
                TransitStop stop = stopMap.get(stopId);
                if (stop != null) {
                    long arrivalTime = stopTime.getArrivalTime() != null ? stopTime.getArrivalTime() : 0L;
                    StopDTO dto = new StopDTO(stop.getName(), stop.getLat(), stop.getLon(), stopId, arrivalTime);
                    result.add(dto);
                }
            }
            return result;

        } catch (Exception e)
        {
            UIUtils.showConnectionDialog();
            return null;
        }


    }
}
