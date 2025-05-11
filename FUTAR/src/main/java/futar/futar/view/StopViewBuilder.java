package futar.futar.view;
import futar.futar.utils.Colors;
import futar.futar.utils.UIUtils;
import futar.futar.model.DepartureDTO;
import java.util.List;

/**
 * megadott megállóhoz tartozó indulások ablak HTML generálása
 */
public class StopViewBuilder {
    /**
     * Megépíti a megállóhoz tartozó indulási információk HTML tartalmát,
     * megjeleníti a megálló nevét, kedvenc gombot, valamint a következő járatokat listázza.
     * @param stopId a megálló egyedi azonosítója
     * @param stopName a megálló neve, amely megjelenik a fejlécben
     * @param departures a megállóból induló járatok listája {@link DepartureDTO} formában
     * @param isFavorite igaz, ha a megálló jelenleg kedvenc
     * @return egy HTML string, amely megjeleníti a teljes popup tartalmát
     */
    public String build(String stopId, String stopName, List<DepartureDTO> departures, boolean isFavorite) {
        StringBuilder html = new StringBuilder();

        html.append("<div style='"
                + "font-family: sans-serif; font-size: 14px; "
                + "max-height: 300px; overflow-y: auto; max-width: 320px; "
                + "padding: 12px; background-color: #fff; "
                + "border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); "
                + "z-index: 9999;'>");

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
            String routeType = dep.getType();
            String routeName = dep.getRoute();

            String typeForColor = routeType.equalsIgnoreCase("SUBWAY") ? routeName.toUpperCase() : routeType.toUpperCase();

            String backgroundColor = Colors.getTitleColor(typeForColor);
            String textColor = Colors.getTextColor(typeForColor);
            System.out.println("[STOPVIEWBUILDER] routeType=" + dep.getType() + " routeName=" + dep.getRoute());


            // Járatszám (kattintható)
            html.append("<div style='text-align: left;'>")
                    .append("<a href='javascript:void(0)' onclick='onRouteClick(\"")
                    .append(UIUtils.escapeJs(dep.getTripId()))
                    .append("\")' style='font-weight: bold; text-decoration: none; color: #000; "
                            + "padding: 2px 6px; border-radius: 4px; background-color: ")
                    .append(backgroundColor)
                    .append(";'>")
                    .append("<span style='color: ").append(textColor).append(";'>")
                    .append(route)
                    .append("</span>")
                    .append("</a>")
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
