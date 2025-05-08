package futar.futar.view;
import futar.futar.utils.UIUtils;
import futar.futar.model.DepartureDTO;

import java.util.List;

public class StopViewBuilder {
    public String build(String stopId, String stopName, List<DepartureDTO> departures, boolean isFavorite) {
        StringBuilder html = new StringBuilder();

        html.append("<div style='"
                + "font-family: sans-serif; font-size: 14px; "
                + "max-height: 300px; overflow-y: auto; max-width: 320px; "
                + "padding: 12px; background-color: #fff; "
                + "border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); "
                + "z-index: 9999;'>");

        // Cím + kedvenc gomb
        html.append("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;'>");
        html.append("<div style='font-weight: bold; font-size: 16px;'>").append(stopName).append("</div>");
        String onclick = "window.java.toggleFavoriteFromPopup('" + UIUtils.escapeJs(stopId) + "', '" + UIUtils.escapeJs(stopName) + "')";
        System.out.println(isFavorite);
        if (isFavorite) {
            html.append("<button onclick=\"").append(onclick).append("\" ...>Törlés kedvencekből</button>");
        } else {
            System.out.println("Stopviewbuilder kedvencekhez adás");
            html.append("<button onclick=\"").append(onclick).append("\" ...>Kedvenc</button>");
        }

        html.append("</div>");

        html.append("<div style='display: grid; grid-template-columns: max-content 1fr; row-gap: 8px; column-gap: 8px;'>");

        for (DepartureDTO dep : departures) {
            String route = dep.getRoute();
            long minutes = dep.getMinutes();
            String displayTime = minutes < 0 ? "" : minutes == 0 ? "MOST" : minutes + " perc múlva";

            // Járatszám (kattintható)
            html.append("<div style='text-align: left;'>")
                    .append("<a href='javascript:void(0)' onclick='onRouteClick(\"")
                    .append(UIUtils.escapeJs(dep.getTripId()))
                    .append("\")' style='font-weight: bold; text-decoration: none; color: #007bff;'>")
                    .append("[").append(route).append("]</a>")
                    .append("</div>");

            // Célállomás és idő
            html.append("<div>")
                    .append("<div style='font-weight: 500;'>")
                    .append("&gt; ").append(dep.getHeadsign())
                    .append("</div>")
                    .append("<div style='font-size: 12px; color: #666;'>")
                    .append(displayTime)
                    .append("</div>")
                    .append("</div>");
        }

        html.append("</div>");
        html.append("</div>");
        return html.toString();
    }


}
