package bugemon.common.models.combat.snapshot;

import java.util.List;

import bugemon.common.models.bugemon.Attack;

/**
 * Immutable snapshot of a single Bugemon's combat-relevant state at a point in time.
 *
 * <p>
 * Used by strategy algorithms (e.g. MiniMax) so they can reason about game state without holding references to mutable
 * {@link bugemon.common.models.combat.CombatBugemon} objects.
 *
 * @param currentHp
 *            remaining hit points
 * @param attacks
 *            list of available attacks
 * @param maxHp
 *            maximum hit points
 * @param effectiveAttack
 *            attack stat after modifiers
 * @param effectiveDefense
 *            defense stat after modifiers
 * @param initiative
 *            initiative value that determines turn order
 */
public record CombatBugemonSnapshot(int currentHp, List<Attack> attacks, int maxHp, int effectiveAttack,
        int effectiveDefense, int initiative) {
    /**
     * Returns {@code true} if this Bugemon still has hit points remaining.
     *
     * @return {@code true} when {@code currentHp > 0}
     */
    public boolean isAlive() {
        return this.currentHp > 0;
    }
}
