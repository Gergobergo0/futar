package futar.futar.utils;

public class Colors {
    public static final String BUS_COLOR = "#449ddd";
    public static final String TRAM_COLOR = "#fcba03";
    public static final String M1_COLOR = "#f9d949";
    public static final String M2_COLOR = "#d23729";
    public static final String M3_COLOR = "#215492";
    public static final String M4_COLOR = "#638141";
    public static final String RAIL_COLOR = "#7f2d69";
    public static final String COACH_COLOR = "#eeae40";
    public static final String BLACK = "#000000";
    public static final String WHITE = "#ffffff"; // javítva: hiányzott a #
    public static final String WALK_COLOR = "#80ede8";
    public static final String TROLLEYBUS_COLOR = "#d23729";

    public static String getTitleColor(String type) {
        if (type == null) return BLACK;

        return switch (type.toUpperCase()) {
            case "WALK" -> WALK_COLOR;
            case "BUS" -> BUS_COLOR;
            case "TRAM" -> TRAM_COLOR;
            case "M1" -> M1_COLOR;
            case "M2" -> M2_COLOR;
            case "M3" -> M3_COLOR;
            case "M4" -> M4_COLOR;
            case "RAIL" -> RAIL_COLOR;
            case "COACH" -> COACH_COLOR;
            case "TROLLEYBUS" -> TROLLEYBUS_COLOR;
            default -> BLACK;
        };
    }

    public static String getBackGroundColor(String type) {
        if (type == null) return "#eeeeee";

        return switch (type.toUpperCase()) {
            case "WALK" -> "#e0f2f1";
            case "BUS" -> "#e3f2fd";
            case "TRAM" -> "#fff3e0";
            case "SUBWAY", "M1", "M2", "M3", "M4" -> "#ede7f6";
            case "RAIL" -> "#f3e5f5";
            case "COACH" -> "#f7e5c6";
            case "TROLLEYBUS" -> "#ffc1bd";
            default -> "#eeeeee";
        };
    }

    public static String getTextColor(String type) {
        if (type == null) return BLACK;

        return switch (type.toUpperCase()) {
            case "M1", "M2", "M3", "M4", "RAIL", "BUS", "TROLLEYBUS" -> WHITE;
            default -> BLACK;
        };
    }
}
