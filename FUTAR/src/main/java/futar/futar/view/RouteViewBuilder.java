package futar.futar.view;

import futar.futar.model.StopDTO;

import java.util.List;

public class RouteViewBuilder {
    public static String build(String routeName, List<StopDTO> stops) {
        StringBuilder html = new StringBuilder("<b>Járat: ").append(routeName).append("</b><br><ul>");
        long now = System.currentTimeMillis() / 1000;

        for (StopDTO stop : stops) {
            long minutes = (stop.getArrivalEpochSeconds() - now) / 60;
            if (minutes < 0) minutes = 0;

            html.append("<li><a href=\"#\" onclick=\"java.javaGetStopDetails('")
                    .append(stop.getId()).append("','")
                    .append(stop.getName()).append("',")
                    .append(stop.getLat()).append(",")
                    .append(stop.getLon()).append("')\">")
                    .append(stop.getName())
                    .append("</a> – ")
                    .append(minutes == 0 ? "MOST" : minutes + " perc múlva")
                    .append("</li>");
        }

        html.append("</ul>");
        return html.toString();
    }

}

