package futar.futar.tests;

import org.openapitools.client.*;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import futar.futar.model.StopDTO;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiTest {
    private static final String BASE_PATH = "https://futar.bkk.hu/api/query/v1/ws";
    private static final String API_KEY = "38ff1614-fa20-40ee-bc79-5c3f5490603c"; // ideiglenes kulcs

    public static void main(String[] args) throws ApiException {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath(BASE_PATH);
        client.addDefaultHeader("key", API_KEY);

        DefaultApi api = new DefaultApi(client);

        TripDetailsOTPMethodResponse response = api.getTripDetails(
                Dialect.OTP,
                null,
                "BKK_1744822480957",
                null,
                null,
                null,
                "1.0",
                null,
                null
        );
        TransitReferences references = response.getData().getReferences();
        System.out.println("References class: " + references.getClass().getName());
    }
}
