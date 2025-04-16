package futar.futar.view;

import futar.futar.model.DepartureDTO;

import java.util.List;

public class DepartureViewBuilder {

    public static String build(List<DepartureDTO> departures) {
        if (departures.isEmpty()) return "<i>Nincs elérhető indulás</i>";

        StringBuilder html = new StringBuilder("<b>Indulások:</b><br><ul>");
        for (DepartureDTO dep : departures) {
            html.append("<li>")
                    .append(dep.getRoute())
                    .append(" - ")
                    .append(dep.getTripHeadsign())
                    .append(" (")
                    .append(dep.getMinutes() == 0 ? "" : dep.getMinutes())
                    .append(dep.getMinutes() == 0 ? "MOST" : " perc múlva")
                    .append(")</li>");
        }
        html.append("</ul>");
        return html.toString();
    }

}
