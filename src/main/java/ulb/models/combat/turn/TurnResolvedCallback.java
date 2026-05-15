package ulb.models.combat.turn;

import java.util.List;

@FunctionalInterface
public interface TurnResolvedCallback {
    void onTurnResolved(List<TurnStep> turnSteps);
}
