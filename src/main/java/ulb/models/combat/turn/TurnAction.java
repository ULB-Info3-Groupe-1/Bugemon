package ulb.models.combat.turn;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.combat.CombatBugemon;
import ulb.models.item.Item;

public sealed interface TurnAction {

    List<TurnStep> accept(TurnActionVisitor visitor);

    record ForfeitAction() implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record SwitchAction(CombatBugemon target) implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record ItemAction(Item item) implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }

    record AttackAction(Attack attack) implements TurnAction {
        public List<TurnStep> accept(TurnActionVisitor visitor) {
            return visitor.visit(this);
        }
    }
}
