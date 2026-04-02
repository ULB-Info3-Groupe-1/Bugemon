package ulb.models.combat;

import java.util.Optional;

import ulb.common.Efficiency;
import ulb.models.bugemon.Attack;
import ulb.models.trainer.Trainer;

/**
 * Immutable snapshot of a single combat turn produced by {@link Combat#turn()}.
 * Check {@link AttackResult#wasAttack()} before reading attack-specific fields on {@code first} or {@code second}.
 *
 * @param allyIsKo {@code true} if the ally's active Bugemon fainted this turn (triggers forced-switch).
 */
public record TurnResult(AttackResult first, Optional<AttackResult> second, boolean allyIsKo) {
    /**
     * Snapshot of one side's action within a turn.
     * When {@link #attack()} is empty the trainer switched or used an item; {@link #efficiency()} is {@code null}.
     */
    public record AttackResult(Trainer attacker, Trainer defender, Optional<Attack> attack, Efficiency efficiency) {
        /** Returns {@code true} if an attack was performed ({@link #attack()} is present). */
        public boolean wasAttack() {
            return this.attack.isPresent();
        }

        public String getAttackName() {
            return this.attack.orElseThrow().name();
        }
    }
}
