package futar.futar.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;

public class ApiClientProvider {
    private static final String BASE_PATH = "https://futar.bkk.hu/api/query/v1/ws";
    private static final String API_KEY = "38ff1614-fa20-40ee-bc79-5c3f5490603c";

    public static ApiClient getClient() {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setConnectTimeout(30_000);
        client.setReadTimeout(30_000);
        client.setWriteTimeout(30_000);
        client.setBasePath(BASE_PATH);
        client.addDefaultHeader("key", API_KEY);
        return client;
    }
}
/*
import okhttp3.OkHttpClient;
import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

public class ApiClientProvider {
    private static final String BASE_PATH = "https://futar.bkk.hu/api/query/v1/ws";
    private static final String API_KEY = "38ff1614-fa20-40ee-bc79-5c3f5490603c";

    public static ApiClient getClient() {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath(BASE_PATH);
        client.setConnectTimeout(30_000);
        client.setReadTimeout(30_000);
        client.setWriteTimeout(30_000);
        client.addDefaultHeader("key", API_KEY);

        try {
            // ❗️ Disable certificate validation (ONLY FOR DEV)
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            client.setHttpClient(okHttpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return client;
    }
}

*/



