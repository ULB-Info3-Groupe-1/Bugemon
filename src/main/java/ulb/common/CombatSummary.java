package ulb.common;

import java.util.List;

import ulb.models.combat.CombatResult;

/** Summary of a combat at the end. */
public record CombatSummary(CombatResult result, List<LevelUpResult> levelUpResults) {
    public boolean hasLeveledUp() {
        return !this.levelUpResults.isEmpty();
    }
}
