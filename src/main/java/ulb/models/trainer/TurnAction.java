package ulb.models.trainer;

import java.util.List;

import ulb.models.combat.TurnPhase;
import ulb.models.combat.TurnStep;

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

    record SwitchAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.PASSIVE;
        }

        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record ItemAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.PASSIVE;
        }

        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record AttackAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.ATTACK;
        }

        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }
}
