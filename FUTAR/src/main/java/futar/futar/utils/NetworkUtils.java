package futar.futar.utils;

import futar.futar.api.ApiClientProvider;
import futar.futar.controller.map.MapInitializer;
import okhttp3.Request;
import okhttp3.Response;
import futar.futar.controller.map.MapController;

public class NetworkUtils {

    public static boolean isApiReachable() {
        try {
            var client = ApiClientProvider.getClient().getHttpClient();
            Request request = new Request.Builder()
                    .url("https://futar.bkk.hu/api/query/v1/ws")  // létező végpont
                    .head()
                    .addHeader("key", "38ff1614-fa20-40ee-bc79-5c3f5490603c")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                System.out.println("🔍 API válasz kód: " + response.code());
                // Bármi, ami nem hálózati hiba (pl. 401 is), azt elfogadjuk válasznak

                return response.code() != 0;
            }
        } catch (Exception e) {
            System.err.println("[HÁLÓZATI HIBA] Futár API elérhetetlen");
            return false;
        }
    }
}
