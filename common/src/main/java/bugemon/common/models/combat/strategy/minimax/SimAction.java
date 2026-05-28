package bugemon.common.models.combat.strategy.minimax;

import bugemon.common.models.item.Item;

/**
 * Immutable value type representing one simulated action considered by the {@link MiniMax} search.
 *
 * <p>
 * Factory methods cover every action kind; use them instead of the canonical constructor to ensure correct sentinel
 * values for unused fields.
 *
 * @param kind
 *            the category of this action
 * @param index
 *            attack index or team-roster index (ignored for {@link SimActionKind#ITEM} and {@link SimActionKind#NONE})
 * @param item
 *            the item to use (non-null only for {@link SimActionKind#ITEM})
 */
public record SimAction(SimActionKind kind, int index, Item item) {
    /**
     * Creates an attack action for the attack at the given index in the active Bugemon's move list.
     *
     * @param attackIndex
     *            zero-based index into the active Bugemon's attack list
     * @return a new {@link SimActionKind#ATTACK} action
     */
    static SimAction attack(int attackIndex) {
        return new SimAction(SimActionKind.ATTACK, attackIndex, null);
    }

    /**
     * Creates a switch action targeting the Bugemon at the given team-roster index.
     *
     * @param teamIndex
     *            zero-based index into the team's Bugemon list
     * @return a new {@link SimActionKind#SWITCH} action
     */
    static SimAction switchTo(int teamIndex) {
        return new SimAction(SimActionKind.SWITCH, teamIndex, null);
    }

    /**
     * Creates an item-use action for the given item.
     *
     * @param item
     *            the item to use; must not be {@code null}
     * @return a new {@link SimActionKind#ITEM} action
     */
    static SimAction useItem(Item item) {
        return new SimAction(SimActionKind.ITEM, -1, item);
    }

    /**
     * Returns a sentinel action representing "no action" (pass / absent action in simultaneous resolution).
     *
     * @return a new {@link SimActionKind#NONE} action
     */
    static SimAction none() {
        return new SimAction(SimActionKind.NONE, -1, null);
    }
}
