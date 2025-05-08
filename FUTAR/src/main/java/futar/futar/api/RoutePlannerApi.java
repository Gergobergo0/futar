package futar.futar.api;

import futar.futar.utils.UIUtils;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;

import java.util.List;

public class RoutePlannerApi {

    private final DefaultApi api;

    public RoutePlannerApi() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    /**
     * Útvonaltervezési kérés elküldése az OTP Dialect alapján.
     */
    public PlanTripResponse planTrip(String fromPlace, String toPlace,
                                     List<TraverseMode> modes,
                                     String date, String time,
                                     boolean arriveBy) {
        try {
            return api.planTrip(
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
        } catch (Exception e) {
            UIUtils.showConnectionDialog();
            return null;
        }
    }
}
