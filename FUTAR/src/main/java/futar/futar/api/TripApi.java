package futar.futar.api;

import futar.futar.model.StopDTO;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TripApi {
    private final DefaultApi api;

    public TripApi() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    public List<StopDTO> getStopsByTrip(String tripId) throws Exception {
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

        List<TransitTripStopTime> stopTimes = response.getData().getEntry().getStopTimes();
        OTPTransitReferences otpRefs = response.getData().getReferences().getOTPTransitReferences();
        Map<String, TransitStop> stopMap = otpRefs.getStops();

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
    }
}
