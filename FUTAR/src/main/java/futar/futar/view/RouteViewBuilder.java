package futar.futar.view;

import futar.futar.model.PathStep;
import futar.futar.model.StopDTO;

import java.util.ArrayList;
import java.util.List;

public class RouteViewBuilder {


    public static String buildAsTextClick(String routeName, List<StopDTO> stops) {
        return buildInternal(routeName, stops);
    }



    private static String buildInternal(String routeName, List<StopDTO> stops) {
        StringBuilder html = new StringBuilder();

        // Külső popup doboz
        html.append("<div style='"
                + "font-family: sans-serif; "
                + "font-size: 14px; "
                + "max-height: 300px; "
                + "overflow-y: auto; "
                + "max-width: 320px; "
                + "padding: 12px; "
                + "background-color: #fff; "
                + "border-radius: 10px; "
                + "box-shadow: 0 4px 12px rgba(0,0,0,0.15); "
                + "z-index: 9999;"
                + "'>");


        html.append("<div style='font-weight: bold; font-size: 16px; margin-bottom: 10px;'>🚌 ")
                .append(routeName)
                .append("</div>");

        long now = System.currentTimeMillis() / 1000;

        for (StopDTO stop : stops) {
            long minutes = (stop.getArrivalEpochSeconds() - now) / 60;
            String displayTime = minutes < 0 ? "" : minutes == 0 ? "MOST" : minutes + " perc múlva";

            // JavaScript hívás átfogalmazása egyszerűbb formára
            html.append("<div style='display: flex; align-items: center; margin-bottom: 8px;'>")
                    .append("<div style='margin-right: 8px;'>📍</div>")
                    .append("<div>")
                    .append("<a href='javascript:void(0)' onclick='onStopClick(\"")
                    .append(stop.getId()).append("\", \"")
                    .append(stop.getName()).append("\", ")
                    .append(stop.getLat()).append(", ")
                    .append(stop.getLon())
                    .append("\")'>")
                    .append("<div style='font-size: 12px; color: #666;'>")
                    .append(displayTime)
                    .append("</div>")
                    .append("</div>")
                    .append("</div>");
        }

        html.append("</div>"); // bezárja az egész popupot
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

