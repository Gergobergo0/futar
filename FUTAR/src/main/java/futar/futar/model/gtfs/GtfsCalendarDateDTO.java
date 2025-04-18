package futar.futar.model.gtfs;

public class GtfsCalendarDateDTO {
    private String serviceId;
    private String date;
    private int exceptionType;


    public GtfsCalendarDateDTO(String serviceId, String date, int exceptionType) {
        this.serviceId = serviceId;
        this.date = date;
        this.exceptionType = exceptionType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(int exceptionType) {
        this.exceptionType = exceptionType;
    }
}
