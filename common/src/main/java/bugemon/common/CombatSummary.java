package bugemon.common;

import java.util.List;

import bugemon.common.models.combat.CombatResult;

/**
 * Immutable summary of a completed combat, pairing the overall {@link CombatResult} with any level-up events that
 * occurred during the battle.
 *
 * @param result
 *            the outcome of the combat (e.g. victory, defeat, forfeit)
 * @param levelUpResults
 *            the list of level-up events triggered by experience gained during the combat; empty if no Bugemon levelled
 *            up
 */
public record CombatSummary(CombatResult result, List<LevelUpResult> levelUpResults) {

    /**
     * Returns {@code true} if at least one Bugemon levelled up during this combat.
     */
    public boolean hasLeveledUp() {
        return !this.levelUpResults.isEmpty();
    }
}
