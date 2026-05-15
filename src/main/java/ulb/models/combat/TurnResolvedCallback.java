package ulb.models.combat;

import java.util.List;

@FunctionalInterface
public interface TurnResolvedCallback {
    void onTurnResolved(List<TurnStep> turnSteps);
}
