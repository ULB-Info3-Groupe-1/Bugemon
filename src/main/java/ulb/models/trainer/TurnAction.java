package ulb.models.trainer;

import ulb.models.combat.TurnPhase;

public sealed interface TurnAction extends Comparable<TurnAction> {

    TurnPhase phase();

    @Override
    default int compareTo(TurnAction other) {
        return this.phase().compareTo(other.phase());
    }

    record ForfeitAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.FORFEIT;
        }
    }

    record SwitchAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.PASSIVE;
        }
    }

    record ItemAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.PASSIVE;
        }
    }

    record AttackAction() implements TurnAction {
        @Override
        public TurnPhase phase() {
            return TurnPhase.ATTACK;
        }
    }
}
