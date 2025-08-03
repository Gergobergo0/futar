package futar.futar.api;

import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;

/**
 * Segédosztály a {@link ApiClient} konfigurálásához
 * <p>
 * Ez az osztály beállítja a kapcsolati időkorlátokat, az alapértelmezett végpontot,
 * valamint hozzáadja a szükséges API-kulcsot minden kéréshez
 */
public class ApiClientProvider {
    /**
     * BKK api futár végpontja
     */
    private static final String BASE_PATH = "https://futar.bkk.hu/api/query/v1/ws";
    /**
     * a generált api kulcs
     */
    private static final String API_KEY = ""; //!!!!!!!!!!!!

    /**
     * Létrehoz és visszaad egy {@link ApiClient}-t.
     * <ul>
     *     <li>30 másodpercess timeoutot állít be</li>
     *<li> BASE_PATH alapértelmezett végpontként</li>
     * <li>hozzáadja az api kulcsot a kéréshez</li>
     *     </ul>
     * @return konfiguráld {@link ApiClient}
     */
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




