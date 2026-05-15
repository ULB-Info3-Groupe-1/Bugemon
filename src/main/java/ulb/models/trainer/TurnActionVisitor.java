package ulb.models.trainer;

import java.util.List;

import ulb.models.combat.TurnStep;
import ulb.models.trainer.TurnAction.AttackAction;
import ulb.models.trainer.TurnAction.ForfeitAction;
import ulb.models.trainer.TurnAction.ItemAction;
import ulb.models.trainer.TurnAction.SwitchAction;

public interface TurnActionVisitor {

    List<TurnStep> visit(ForfeitAction action);

    List<TurnStep> visit(SwitchAction action);

    List<TurnStep> visit(ItemAction action);

    List<TurnStep> visit(AttackAction action);

}
