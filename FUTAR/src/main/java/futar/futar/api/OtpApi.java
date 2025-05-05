package futar.futar.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import futar.futar.model.PathStep;
import futar.futar.model.StopTimeDTO;
import futar.futar.model.TransitRoute;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class OtpApi {

    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String baseUrl = "http://localhost:8080/otp/routers/default";

    public OtpApi() {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    public TransitRoute getPlan(String fromCoords, String toCoords, String time, String date, String mode,
                                boolean arriveBy, double walkSpeed, int maxWalkDistance) throws Exception {
        HttpUrl url = HttpUrl.parse(baseUrl + "/plan").newBuilder()
                .addQueryParameter("fromPlace", fromCoords)
                .addQueryParameter("toPlace", toCoords)
                .addQueryParameter("time", time)
                .addQueryParameter("date", date)
                .addQueryParameter("arriveBy", "false")
                .addQueryParameter("mode", "TRANSIT,WALK")
                .addQueryParameter("maxWalkDistance", String.valueOf(maxWalkDistance))
                .addQueryParameter("walkSpeed", String.valueOf(walkSpeed))
                .addQueryParameter("numItineraries", "1")
                .addQueryParameter("waitReluctance", "1.0")
                .addQueryParameter("walkReluctance", "2.5")
                .addQueryParameter("optimize", "QUICK")



                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        ZoneId zone = ZoneId.of("Europe/Budapest");

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Request failed: " + response.code());
            }

            String jsonData = response.body().string();
            System.out.println("[DEBUG] OTP válasz:");
            System.out.println(jsonData);

            JsonObject json = gson.fromJson(jsonData, JsonObject.class);
            JsonObject plan = json.getAsJsonObject("plan");
            if (plan == null || !plan.has("itineraries")) {
                return new TransitRoute();
            }

            JsonArray itineraries = plan.getAsJsonArray("itineraries");
            if (itineraries.isEmpty()) return new TransitRoute();

            JsonObject best = null;
            for (int i = 0; i < itineraries.size(); i++) {
                JsonObject itinerary = itineraries.get(i).getAsJsonObject();
                JsonArray legs = itinerary.getAsJsonArray("legs");
                System.out.println("------ Itinerary " + i + " ------");

                for (int j = 0; j < legs.size(); j++) {
                    JsonObject leg = legs.get(j).getAsJsonObject();
                    if (leg.get("transitLeg").getAsBoolean()) {
                        String tripId = leg.has("tripId") ? leg.get("tripId").getAsString() : "N/A";
                        String serviceDate = leg.has("serviceDate") ? leg.get("serviceDate").getAsString() : "N/A";
                        String route = leg.has("routeShortName") ? leg.get("routeShortName").getAsString() : "";
                        String headsign = leg.has("headsign") ? leg.get("headsign").getAsString() : "";
                        String serviceId = leg.has("ServiceId") ? leg.get("ServiceId").getAsString() : "";
                        // TRIP ID → SERVICE ID keresés

                        System.out.println("[DEBUG] TripID: " + tripId +
                                " | Route: " + route +
                                " | Headsign: " + headsign +
                                " | ServiceId: " + serviceId +
                                " | OTP ServiceDate: " + serviceDate);
                    }
                    if (leg.get("transitLeg").getAsBoolean()) {
                        best = itinerary;
                        break;
                    }
                }

                if (best != null) break;
            }
            if (best == null) {
                best = itineraries.get(0).getAsJsonObject();
            }

            TransitRoute route = new TransitRoute();
            JsonArray legs = best.getAsJsonArray("legs");

            LocalTime startTime = null;

            for (int i = 0; i < legs.size(); i++) {
                JsonObject leg = legs.get(i).getAsJsonObject();
                String modeStr = leg.get("mode").getAsString();
                String tripId = leg.has("tripId") ? leg.get("tripId").getAsString() : "walk";
                String routeId = leg.has("route") ? leg.get("route").getAsString() : "";
                String from = leg.getAsJsonObject("from").get("name").getAsString();
                String to = leg.getAsJsonObject("to").get("name").getAsString();

                LocalTime departure = Instant.ofEpochMilli(leg.get("startTime").getAsLong())
                        .atZone(zone).toLocalTime();

                LocalTime arrival = Instant.ofEpochMilli(leg.get("endTime").getAsLong())
                        .atZone(zone).toLocalTime();

                if (startTime == null) startTime = departure;

                String routeShortName = leg.has("routeShortName") ? leg.get("routeShortName").getAsString() : "";
                String headsign = leg.has("headsign") ? leg.get("headsign").getAsString() : "";
                String label;
                String serviceDate = leg.has("serviceDate") ? leg.get("serviceDate").getAsString() : "UNKNOWN";

                System.out.println("[DEBUG] OTP TripID: " + tripId + " | ServiceDate: " + serviceDate);

                if (!routeShortName.isEmpty() && !headsign.isEmpty()) {
                    label = routeShortName + " • " + headsign;
                } else if (!routeId.isEmpty()) {
                    label = routeId;
                } else {
                    label = modeStr;
                }

                String stopId = leg.getAsJsonObject("from").has("stopId")
                        ? leg.getAsJsonObject("from").get("stopId").getAsString()
                        : "";

                route.addStep(new PathStep(label, from, to, departure.toString(), arrival.toString(), tripId, modeStr, stopId));
            }

            LocalTime endTime = Instant.ofEpochMilli(best.get("endTime").getAsLong())
                    .atZone(zone).toLocalTime();
            route.setArrivalTime(endTime);
            if (startTime != null) route.setStartTime(startTime);

            return route;
        }
    }




/*
    // Overloaded method for backward compatibility
    public TransitRoute getPlan(String fromCoords, String toCoords, String time, String date, String mode) throws Exception {
        return getPlan(fromCoords, toCoords, time, date, mode, false);
    }*/

    public List<StopTimeDTO> getStopTimes(String tripId) throws Exception {
        HttpUrl url = HttpUrl.parse(baseUrl + "/index/trips/" + tripId + "/stoptimes").newBuilder().build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Request failed: " + response.code());
            }

            String jsonData = response.body().string();
            JsonArray array = gson.fromJson(jsonData, JsonArray.class);
            List<StopTimeDTO> stopTimes = new ArrayList<>();

            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                String stopId = obj.getAsJsonObject("stop").get("id").getAsString();
                String stopName = obj.getAsJsonObject("stop").get("name").getAsString();
                long arrivalTime = obj.has("arrival") ? obj.get("arrival").getAsLong() : 0L;

                StopTimeDTO dto = new StopTimeDTO(stopId, stopName, arrivalTime);
                stopTimes.add(dto);
            }
            return stopTimes;
        }
    }
}
