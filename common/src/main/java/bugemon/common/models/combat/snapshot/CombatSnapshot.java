package bugemon.common.models.combat.snapshot;

import java.util.Map;

import bugemon.common.models.combat.CombatTeam;
import bugemon.common.models.item.Item;

/**
 * Immutable snapshot of the full combat state as seen from one side of the fight.
 *
 * <p>
 * The naming convention is relative to the <b>AI</b>: {@code aiTeam} is the AI's own team and {@code playerTeam} is the
 * opposing (human) team. Use {@link #fromAiPerspective} to construct an instance from live {@link CombatTeam} objects.
 *
 * @param playerTeam
 *            snapshot of the opponent (human player) team
 * @param aiTeam
 *            snapshot of the AI-controlled team
 * @param aiInventory
 *            AI's current item inventory keyed by item with quantity values
 * @param opponentInventory
 *            opponent's current item inventory keyed by item with quantity values
 */
public record CombatSnapshot(TeamSnapshot playerTeam, TeamSnapshot aiTeam, Map<Item, Integer> aiInventory,
        Map<Item, Integer> opponentInventory) {

    /**
     * Creates a {@code CombatSnapshot} from live {@link CombatTeam} objects, viewed from the AI's perspective.
     *
     * @param aiTeam
     *            the AI's live team
     * @param opponentTeam
     *            the opponent's live team
     * @param aiInventory
     *            AI's current item inventory
     * @param opponentInventory
     *            opponent's current item inventory
     * @return a new snapshot with teams frozen at the moment of the call
     * @throws IllegalStateException
     *             if either team is {@code null}
     */
    public static CombatSnapshot fromAiPerspective(CombatTeam aiTeam, CombatTeam opponentTeam,
            Map<Item, Integer> aiInventory, Map<Item, Integer> opponentInventory) {
        if (opponentTeam == null || aiTeam == null) {
            throw new IllegalStateException("Both teams must be non-null to create a CombatSnapshot.");
        }
        return new CombatSnapshot(opponentTeam.getSnapshot(), aiTeam.getSnapshot(), aiInventory, opponentInventory);
    }

    /**
     * Returns the snapshot of the AI's currently active Bugemon.
     *
     * @return active AI Bugemon snapshot
     */
    public CombatBugemonSnapshot aiActive() {
        return this.aiTeam.active();
    }

    /**
     * Returns the snapshot of the opponent's currently active Bugemon.
     *
     * @return active opponent Bugemon snapshot
     */
    public CombatBugemonSnapshot opponentActive() {
        return this.playerTeam.active();
    }

    /**
     * Returns {@code true} if all bugemons on the AI's team have fainted.
     *
     * @return {@code true} when the AI has no alive bugemons remaining
     */
    public boolean isAiDefeated() {
        for (CombatBugemonSnapshot bugemon : this.aiTeam.bugemons()) {
            if (bugemon.isAlive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all bugemons on the opponent's team have fainted.
     *
     * @return {@code true} when the opponent has no alive bugemons remaining
     */
    public boolean isOpponentDefeated() {
        for (CombatBugemonSnapshot bugemon : this.playerTeam.bugemons()) {
            if (bugemon.isAlive()) {
                return false;
            }
        }
        return true;
    }

}
