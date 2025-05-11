package futar.futar.view;

import futar.futar.model.StopDTO;
import futar.futar.utils.Colors;
import futar.futar.utils.UIUtils;

import java.util.List;

/**
 * html alapú tartalmat generál egy járat útvonalának megjelenítéséhez
 */
public class RouteViewBuilder {
    /**
     * generálja a járat állomásainak HTML nézetét,
     * @param routeName járat neve pl M4
     * @param routeType járat típusa pl BUS
     * @param stops járat megállói
     * @return
     */
    public String build(String routeName, String routeType, List<StopDTO> stops) {
        StringBuilder html = new StringBuilder();
        String backgroundColor = Colors.getTitleColor(routeType);
        String textColor = Colors.getTextColor(routeType);
        System.out.println("[RouteViewBuilder] routeType=" + routeType);


        html.append("<div style='font-family: sans-serif; font-size: 14px; "
                + "max-height: 300px; overflow-y: auto; max-width: 320px; "
                + "padding: 12px; background-color: #fff; border-radius: 10px; "
                + "box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 9999;'>");

        html.append("<div style='font-weight: bold; font-size: 16px; margin-bottom: 10px; "
                        + "padding: 4px 8px; border-radius: 6px; background-color: " + backgroundColor
                        + "; color: " + textColor + "; display: inline-block;'>")
                .append(routeName)
                .append("</div>");

        long now = System.currentTimeMillis() / 1000;

        for (int i = 0; i < stops.size(); ++i) {
            StopDTO stop = stops.get(i);
            long minutes = (stop.getArrivalEpochSeconds() - now) / 60;

            String displayTime = i == 0 ? "" : minutes == 0 ? "MOST" : minutes < 0 ? (minutes*-1) + " perce elment" : minutes + " perc múlva";
            String displayIcon = i == 0 ? "[-]" : minutes == 0 ? "[!]" : minutes < 0 ? "[-]" : "[+]";

            html.append("<div style='display: flex; align-items: center; margin-bottom: 8px;'>")
                    .append("<div style='margin-right: 8px;'>")
                    .append(displayIcon)
                    .append("</div><div>")
                    .append("<a href='javascript:void(0)' onclick='onStopClick(\"")
                    .append(UIUtils.escapeJs(stop.getId())).append("\", \"")
                    .append(UIUtils.escapeJs(stop.getName())).append("\", ")
                    .append(stop.getLat()).append(", ").append(stop.getLon()).append(")'>")
                    .append(stop.getName()).append("</a>")
                    .append("<div style='font-size: 12px; color: #666;'>")
                    .append(displayTime)
                    .append("</div>")
                    .append("</div>")
                    .append("</div>");
        }

        html.append("</div>");
        return html.toString();
    }

}
