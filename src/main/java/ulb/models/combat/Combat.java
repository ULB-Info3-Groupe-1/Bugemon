package ulb.models.combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.trainer.TurnAction;

public class Combat {
    private static final Logger LOG = LoggerFactory.getLogger(Combat.class);

    private final CombatTeam playerTeam;
    private final CombatTeam opponentTeam;

    public Combat(CombatTeam playerTeam, CombatTeam opponentTeam) {
        this.playerTeam = playerTeam;
        this.opponentTeam = opponentTeam;
    }

    public void requestActions(TurnCallback callback) {
        CombatContext playerCtx = new CombatContext(this.playerTeam, this.opponentTeam);
        CombatContext opponentCtx = new CombatContext(this.opponentTeam, this.playerTeam);

        // magic stuff to call onBothActionsReady only once both actions are ready

        // Single element arrays because of an odd java rule with local variables and enclosing scopes
        TurnAction[] playerAction = new TurnAction[1];
        TurnAction[] opponentAction = new TurnAction[1];

        ActionCallback playerCb = action -> {
            playerAction[0] = action;
            if (opponentAction[0] != null) {
                callback.onBothActionsReady(playerAction[0], opponentAction[0]);
            }
        };
        ActionCallback opponentCb = action -> {
            opponentAction[0] = action;
            if (playerAction[0] != null) {
                callback.onBothActionsReady(playerAction[0], opponentAction[0]);
            }
        };

        // TODO: send the callbacks
    }
}
