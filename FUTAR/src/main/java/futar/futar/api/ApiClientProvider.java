package futar.futar.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;

public class ApiClientProvider {
    private static final String BASE_PATH = "https://futar.bkk.hu/api/query/v1/ws";
    private static final String API_KEY = "38ff1614-fa20-40ee-bc79-5c3f5490603c"; // vagy külön configból

    public static ApiClient getClient() {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setConnectTimeout(30_000);
        client.setReadTimeout(30_000);
        client.setWriteTimeout(30_000);        client.setBasePath(BASE_PATH);
        client.addDefaultHeader("key", API_KEY);
        return client;
    }
}
