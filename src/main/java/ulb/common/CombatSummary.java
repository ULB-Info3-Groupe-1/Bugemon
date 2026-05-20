package ulb.common;

import java.util.List;

import ulb.models.combat.CombatResult;

/** Résumé complet d'un combat terminé. */
public record CombatSummary(CombatResult result, List<LevelUpResult> levelUpResults) {
    public boolean hasLeveledUp() {
        return !this.levelUpResults.isEmpty();
    }
}
