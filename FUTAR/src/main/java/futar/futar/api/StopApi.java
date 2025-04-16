package futar.futar.api;

import futar.futar.api.ApiClientProvider;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import futar.futar.model.StopDTO;

import java.util.List;

public class StopApi {
    private final DefaultApi api;

    public StopApi() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    public StopsForLocationResponse getStopsNear(double lat, double lon, int radius) throws Exception {
        return api.getStopsForLocation(
                Dialect.OTP,
                (float) lat,
                (float) lon,
                300f,
                null, null,
                null, null, null, null, null
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
