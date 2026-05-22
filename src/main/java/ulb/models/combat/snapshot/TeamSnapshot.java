package ulb.models.combat.snapshot;

import java.util.List;

/**
 * Immutable snapshot of a team's state at a point in time, including the roster and the currently active Bugemon.
 *
 * @param bugemons
 *            all Bugemon snapshots in this team (alive and fainted)
 * @param active
 *            the currently active Bugemon's snapshot
 */
public record TeamSnapshot(List<CombatBugemonSnapshot> bugemons, CombatBugemonSnapshot active) {
    /**
     * Returns the number of Bugemon in this team (alive and fainted).
     *
     * @return total team size
     */
    public int size() {
        return this.bugemons.size();
    }
}
