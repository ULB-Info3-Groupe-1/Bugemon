package ulb.models.combat.turn;

import java.util.List;

import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnAction.SwitchAction;

public interface TurnActionVisitor {

    List<TurnStep> visit(ForfeitAction action);

    List<TurnStep> visit(SwitchAction action);

    List<TurnStep> visit(ItemAction action);

    List<TurnStep> visit(AttackAction action);

}
