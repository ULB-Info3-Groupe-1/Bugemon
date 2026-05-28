package bugemon.common.models.combat.turn;

import java.util.List;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.common.models.item.Item;

/**
 * Represents a decision made by one combatant for a single turn.
 *
 * <p>
 * Implemented as a sealed interface with four permitted record variants:
 * <ul>
 * <li>{@link ForfeitAction} — the combatant concedes the battle.</li>
 * <li>{@link SwitchAction} — the combatant swaps the active Bugemon.</li>
 * <li>{@link ItemAction} — the combatant uses an item from the inventory.</li>
 * <li>{@link AttackAction} — the combatant's active Bugemon performs an attack.</li>
 * </ul>
 *
 * <p>
 * Each variant is resolved by calling {@link #accept(TurnActionVisitor)}, which dispatches to the matching
 * {@link TurnActionVisitor} overload and returns the list of {@link TurnStep} events produced.
 */
public sealed interface TurnAction {

    /**
     * Dispatches this action to the appropriate visitor overload and returns the resulting sequence of {@link TurnStep}
     * events.
     *
     * @param visitor
     *            the visitor that resolves this action; never {@code null}
     * @return an ordered list of steps describing what happened; never {@code null}
     */
    List<TurnStep> accept(TurnActionVisitor visitor);

    /**
     * Signals that the combatant forfeits the current battle.
     */
    record ForfeitAction() implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    /**
     * Signals that the combatant switches its active Bugemon.
     *
     * @param target
     *            the {@link CombatBugemon} to switch in; must be alive and on the bench
     */
    record SwitchAction(CombatBugemon target) implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    /**
     * Signals that the combatant uses an item.
     *
     * @param item
     *            the {@link Item} to apply from the inventory
     */
    record ItemAction(Item item) implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    /**
     * Signals that the combatant's active Bugemon performs an attack.
     *
     * @param attack
     *            the {@link Attack} to execute
     */
    record AttackAction(Attack attack) implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }
}
