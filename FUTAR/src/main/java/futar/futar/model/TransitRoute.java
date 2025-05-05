// TransitRoute.java
package futar.futar.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransitRoute {
    private List<PathStep> steps = new ArrayList<>();
    private LocalTime arrivalTime;
    private LocalTime startTime; // <-- ezt add hozzá

    public void addStep(PathStep step) {
        steps.add(step);
    }

    public List<PathStep> getSteps() {
        return steps;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
}
