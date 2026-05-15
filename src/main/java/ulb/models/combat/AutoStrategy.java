package ulb.models.combat;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.trainer.TurnAction;

public class AutoStrategy implements CombatStrategy {
    private final Random random;

    public AutoStrategy(Random random) {
        this.random = random;
    }

    @Override
    public void chooseAction(CombatContext ctx, ActionCallback callback) {
        CombatBugemon active = ctx.allyTeam().getActive();
        List<Attack> attacks = active.getAttacks();
        Attack attack = attacks.get(this.random.nextInt(attacks.size()));

        callback.onActionChosen(new TurnAction.AttackAction(attack));
    }

    @Override
    public void chooseSwitch(CombatContext ctx, ActionCallback callback) {
        List<CombatBugemon> available = ctx.allyTeam().getAvailable();

        if (available.isEmpty()) {
            throw new IllegalStateException("AutoStrategy must switch but has no avaible bugemon.");
        }

        CombatBugemon chosen = available.get(this.random.nextInt(available.size()));
        callback.onActionChosen(new TurnAction.SwitchAction(chosen));
    }
}
