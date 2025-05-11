// TransitRoute.java
package futar.futar.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * teljes útvonal osztálya , ami több PathStep-ból áll
 */
public class TransitRoute {
    private List<PathStep> steps = new ArrayList<>();
    private LocalTime arrivalTime;
    private LocalTime startTime; // <-- ezt add hozzá
    private String mode;
    /**
     * Hozzáad egy útvonal lépést a jelenlegi listához.
     *
     * @param step az új {@link PathStep} lépés
     */
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }
}
