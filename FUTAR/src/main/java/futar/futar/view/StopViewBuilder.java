package futar.futar.view;

import futar.futar.model.DepartureDTO;

import java.util.List;

public class StopViewBuilder {

    public static String buildFloatingPopup(String stopName, List<DepartureDTO> departures) {
        StringBuilder html = new StringBuilder();


        html.append("<div style='"
                + "font-family: sans-serif; font-size: 14px; "
                + "max-height: 300px; overflow-y: auto; max-width: 320px; "
                + "padding: 12px; background-color: #fff; "
                + "border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); "
                + "z-index: 9999;'>");



        html.append("<div style='font-weight: bold; font-size: 16px; margin-bottom: 10px;'>🚏 ")
                .append(stopName).append("</div>");

        long now = System.currentTimeMillis() / 1000;
        System.out.println("KATTATOKM");

        for (DepartureDTO dep : departures) {
            long minutes = dep.getMinutes();
            String displayTime = minutes < 0 ? "" : minutes == 0 ? "MOST" : minutes + " perc múlva";

            html.append("<div style='display: flex; align-items: center; margin-bottom: 8px;'>")
                    .append("<div style='margin-right: 8px;'>🚌</div>")
                    .append("<div>")
                    .append("<a href='javascript:void(0)' onclick='onRouteClick(\"")
                    .append(escapeJs(dep.getTripId()))
                    .append("\")' style='text-decoration: none; font-weight: 500; color: #007bff;'>")
                    .append(dep.getTripHeadsign()).append(" → ").append(dep.getHeadsign()).append("</a>")                    .append("<div style='font-size: 12px; color: #666;'>")
                    .append(displayTime)
                    .append("</div>")
                    .append("</div>")
                    .append("</div>");
        }

        html.append("</div>");
        return html.toString();
    }

    private static String escapeJs(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "")
                .replace("\r", "");
    }
}
