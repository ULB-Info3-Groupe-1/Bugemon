package ulb.models.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TurnResult {
    private final List<TurnStep> steps;

    public TurnResult() {
        this.steps = new ArrayList<>();
    }

    public void addStep(TurnStep step) {
        this.steps.add(step);
    }

    public Iterator<TurnStep> steps() {
        return this.steps.iterator();
    }
}
