package ulb.models.trainer;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Item;

/**
 * Sealed interface representing the action a {@link Trainer} takes for a given combat turn. Passive actions (switch,
 * forfeit, item use) return {@code false} from {@link #isAttack()}; the combat engine then skips damage calculation for
 * them while still allowing the opponent to retaliate.
 */
public sealed interface TurnAction permits TurnAction.AttackAction, TurnAction.SwitchAction, TurnAction.ForfeitAction, TurnAction.UseItemAction {
    /**
     * Returns {@code true} if this action generates a hit on the opponent. Only {@link AttackAction} returns
     * {@code true}.
     */
    boolean isAttack();

    // ── Concrete action types ─────────────────────────────────────────────────

    record AttackAction(Attack attack) implements TurnAction {
        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public String toString() {
            return "attack(" + this.attack.name() + ")";
        }
    }

    /** The switching trainer does not deal damage this turn, but the opponent still attacks. */
    record SwitchAction(Bugemon target) implements TurnAction {
        @Override
        public boolean isAttack() {
            return false;
        }

        @Override
        public String toString() {
            return "switch(" + this.target.getName() + ")";
        }
    }

    /**
     * Forfeit kills the trainer's whole team so {@link ulb.models.combat.Combat#getWinner()} returns the opponent.
     */
    record ForfeitAction() implements TurnAction {
        @Override
        public boolean isAttack() {
            return false;
        }

        @Override
        public String toString() {
            return "forfeit";
        }
    }

    record UseItemAction(Item item) implements TurnAction {
        @Override
        public boolean isAttack() {
            // Item are never used against the opponent
            return false;
        }

        @Override
        public String toString() {
            return "item(" + this.item.name() + ")";
        }
    }
}
