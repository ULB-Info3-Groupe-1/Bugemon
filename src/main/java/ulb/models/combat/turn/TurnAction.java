package ulb.models.combat.turn;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.combat.CombatBugemon;
import ulb.models.item.Item;

public sealed interface TurnAction extends Comparable<TurnAction> {

    TurnPhase phase();

    @Override
    default int compareTo(TurnAction other) {
        return this.phase().compareTo(other.phase());
    }

    List<TurnStep> accept(TurnActionVisitor visitor);

    record ForfeitAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.FORFEIT;
        }

        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record SwitchAction(CombatBugemon target) implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.PASSIVE;
        }

        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record ItemAction(Item item) implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.PASSIVE;
        }

        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record AttackAction(Attack attack) implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.ATTACK;
        }

        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }
}
