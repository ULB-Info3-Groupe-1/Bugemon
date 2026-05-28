package bugemon.common.models.combat.strategy;

import java.util.List;

import bugemon.common.Configuration;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.common.models.combat.CombatTeam;
import bugemon.common.models.combat.snapshot.CombatSnapshot;
import bugemon.common.models.combat.strategy.minimax.MiniMax;
import bugemon.common.models.combat.strategy.minimax.SimAction;
import bugemon.common.models.combat.strategy.minimax.SimActionKind;
import bugemon.common.models.combat.turn.ActionCallback;
import bugemon.common.models.combat.turn.TurnAction;
import bugemon.common.models.combat.utils.CombatContext;
import bugemon.common.models.item.Inventory;

/**
 * {@link CombatStrategy} that uses a {@link MiniMax} tree search to select the best action.
 *
 * <p>
 * On each decision point the strategy freezes the current combat state into a
 * {@link bugemon.common.models.combat.snapshot.CombatSnapshot} and delegates to {@link MiniMax#chooseBestAction} with the
 * configured search depth ({@link bugemon.common.Configuration.Game#MIN_MAX_MAXIMAL_DEPTH}). If the minimax result is a switch but
 * the switch is unavailable, the strategy falls back to the first available Bugemon.
 */
public class MiniMaxStrategy implements CombatStrategy {

    /** {@inheritDoc} */
    @Override
    public void chooseAction(CombatContext ctx, ActionCallback callback) {
        CombatTeam ally = ctx.allyTeam();
        CombatTeam opp = ctx.opponentTeam();
        Inventory allyInv = ctx.allyInventory();
        Inventory oppInv = ctx.opponentInventory();

        CombatSnapshot snapshot = CombatSnapshot.fromAiPerspective(ally, opp, oppInv.getMap(), allyInv.getMap());
        MiniMax mini = new MiniMax(Configuration.Game.MIN_MAX_MAXIMAL_DEPTH);
        // choose for the AI-controlled team explicitly
        SimAction action = mini.chooseBestAction(snapshot, true);

        TurnAction ta = switch (action.kind()) {
            case ATTACK -> {
                var atkList = ally.getActive().getAttacks();
                int idx = Math.clamp(action.index(), 0, atkList.size() - 1);
                yield new TurnAction.AttackAction(atkList.get(idx));
            }
            case SWITCH -> {
                List<CombatBugemon> available = ally.getAvailable();
                if (available.isEmpty()) {
                    throw new IllegalStateException("No available bugemon to switch");
                }
                int idx = Math.clamp(action.index(), 0, available.size() - 1);
                yield new TurnAction.SwitchAction(available.get(idx));
            }
            case ITEM -> new TurnAction.ItemAction(action.item());
            default -> new TurnAction.ForfeitAction();
        };

        callback.onActionChosen(ta);
    }

    /** {@inheritDoc} */
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
        MiniMax mini = new MiniMax(Configuration.Game.MIN_MAX_MAXIMAL_DEPTH);
        SimAction action = mini.chooseBestAction(snapshot, true);

        if (action.kind() == SimActionKind.SWITCH) {
            int idx = Math.clamp(action.index(), 0, available.size() - 1);
            callback.onActionChosen(new TurnAction.SwitchAction(available.get(idx)));
            return;
        }

        // fallback - pick first available
        callback.onActionChosen(new TurnAction.SwitchAction(available.get(0)));
    }

}
