package futar.futar.view;

import futar.futar.model.DepartureDTO;

import java.util.List;

public class DepartureViewBuilder {


    public static String build(List<DepartureDTO> departures, String buttonText) {
        if (departures.isEmpty()) return "<i>Nincs elérhető indulás</i>";

        StringBuilder html = new StringBuilder("<b>Indulások:</b><br><ul>");
        for (DepartureDTO dep : departures) {
            html.append("<li>")
                    .append("<a href=\"#\" onclick=\"onRouteClick('")
                    .append(dep.getTripId())  // pl. "BKK_tripID"
                    .append("')\">")
                    .append(dep.getRoute())
                    .append("</a> - ")
                    .append(dep.getTripHeadsign())
                    .append(" (")
                    .append(dep.getMinutes() == 0 ? "" : dep.getMinutes())
                    .append(dep.getMinutes() == 0 ? "MOST" : " perc múlva")
                    .append(")</li>");
        }
        html.append("</ul>");
        html.append("<button id='favoriteButton' onclick=\"java.toggleFavorite()\">")
                .append(buttonText)
                .append("</button>");
        return html.toString();
    }

}
