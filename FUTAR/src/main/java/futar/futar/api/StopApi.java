package futar.futar.api;

import futar.futar.utils.UIUtils;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Dialect;
import org.openapitools.client.model.StopsForLocationResponse;


/**
 * API kérések a megállók kereséséhez
 * <p>
 *     Az osztály lehetővétesz leérdezéseket koordináták vagy név alapján
 * </p>
 */
public class StopApi {
    /**
     * API végpontot kezelő kliens
     */
    private final DefaultApi api;

    /**
     * Konstruktor, létrehoz egy új api klienst
     */
    public StopApi() {
        this.api = new DefaultApi(ApiClientProvider.getClient());
    }

    /**
     * megállókat keres a megadott környezetben
     * @param lat - a keresés középpontjának szélességi (latitude) koordinátája
     * @param lon - a keresés középpontjának hosszúsági (longitude) koordinátája
     * @param radius a keresési sugár mérete méterben
     * @return {@link StopsForLocationResponse} objektum, ha sikeres;
     *         {@code null} ha sikertelen és megjeleníti a felhasználónak a áhálózati hibát
     */
    public StopsForLocationResponse getStopsNear(double lat, double lon, int radius){
        try {
            return api.getStopsForLocation(
                    Dialect.OTP, (float) lat, (float) lon, (float) radius,
                    null, null, null, null, null, null, null
            );
        } catch (Exception e){
            UIUtils.showConnectionDialog();
            return null;
        }


    }

    /**
     * Megállókat keres név alapján
     * @param query keresett megálló neve vagy részlete (pl Astoria: Astoria M, Budapest, Astoria stb...
     * @return {@link StopsForLocationResponse} ha sikeres
     *         {@code null} ha sikertelen és hibaüzenet dob a felhasználónak
     */
    public StopsForLocationResponse getStopsByName(String query){
        try {
            return api.getStopsForLocation(
                    Dialect.OTP,
                    null, null, null,
                    null, null,
                    query,
                    10,
                    null, null, null
            );
        } catch (Exception e)
        {
            UIUtils.showConnectionDialog();
            return null;
        }

    }
}