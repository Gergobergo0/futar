package futar.futar.api;

import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Dialect;
import org.openapitools.client.model.StopsForLocationResponse;

public class StopApi {
    private final DefaultApi api;

    public StopApi() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    public StopsForLocationResponse getStopsNear(double lat, double lon, int radius) throws Exception {
        return api.getStopsForLocation(
                Dialect.OTP, (float) lat, (float) lon, (float) radius,
                null, null, null, null, null, null, null
        );
    }

    public StopsForLocationResponse getStopsByName(String query) throws Exception {
        return api.getStopsForLocation(
                Dialect.OTP,
                null, null, null,
                null, null,
                query,
                10,
                null, null, null
        );
    }
}