package ulb.models.combat.strategy;

import java.util.List;

import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.combat.snapshot.CombatSnapshot;
import ulb.models.combat.strategy.minimax.MiniMax;
import ulb.models.combat.strategy.minimax.SimAction;
import ulb.models.combat.strategy.minimax.SimActionKind;
import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.turn.TurnAction;
import ulb.models.combat.utils.CombatContext;
import ulb.models.item.Inventory;

public class MiniMaxStrategy implements CombatStrategy {
    public MiniMaxStrategy() {
    }

    @Override
    public void chooseAction(CombatContext ctx, ActionCallback callback) {
        CombatTeam ally = ctx.allyTeam();
        CombatTeam opp = ctx.opponentTeam();
        Inventory allyInv = ctx.allyInventory();
        Inventory oppInv = ctx.opponentInventory();

        CombatSnapshot snapshot = CombatSnapshot.fromAiPerspective(ally, opp, oppInv.getMap(), allyInv.getMap());
        MiniMax mini = new MiniMax(4);
        // choose for the AI-controlled team explicitly
        SimAction action = mini.chooseBestAction(snapshot, true);

        TurnAction ta;
        switch (action.kind()) {
            case ATTACK -> {
                var atkList = ally.getActive().getAttacks();
                int idx = Math.max(0, Math.min(action.index(), atkList.size() - 1));
                ta = new TurnAction.AttackAction(atkList.get(idx));
            }
            case SWITCH -> {
                List<CombatBugemon> available = ally.getAvailable();
                if (available.isEmpty()) {
                    throw new IllegalStateException("No available bugemon to switch");
                }
                int idx = Math.max(0, Math.min(action.index(), available.size() - 1));
                ta = new TurnAction.SwitchAction(available.get(idx));
            }
            case ITEM -> ta = new TurnAction.ItemAction(action.item());
            default -> ta = new TurnAction.ForfeitAction();
        }

        callback.onActionChosen(ta);
    }

    @Override
    public void chooseSwitch(CombatContext ctx, ActionCallback callback) {
        CombatTeam ally = ctx.allyTeam();
        CombatTeam opp = ctx.opponentTeam();
        Inventory allyInv = ctx.allyInventory();
        Inventory oppInv = ctx.opponentInventory();


        List<CombatBugemon> available = ally.getAvailable();
        if (available.isEmpty()) {
            throw new IllegalStateException("MiniMaxStrategy must switch but has no available bugemon.");
        }

        CombatSnapshot snapshot = CombatSnapshot.fromAiPerspective(ally, opp, oppInv.getMap(), allyInv.getMap());
        MiniMax mini = new MiniMax(4);
        SimAction action = mini.chooseBestAction(snapshot, true);

        if (action.kind() == SimActionKind.SWITCH) {
            int idx = Math.max(0, Math.min(action.index(), available.size() - 1));
            callback.onActionChosen(new TurnAction.SwitchAction(available.get(idx)));
            return;
        }

        // fallback - pick first available
        callback.onActionChosen(new TurnAction.SwitchAction(available.get(0)));
    }

}
