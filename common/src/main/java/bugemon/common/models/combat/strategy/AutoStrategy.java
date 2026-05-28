package bugemon.common.models.combat.strategy;

import java.util.List;
import java.util.Random;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.common.models.combat.turn.ActionCallback;
import bugemon.common.models.combat.turn.TurnAction;
import bugemon.common.models.combat.utils.CombatContext;

/**
 * {@link CombatStrategy} that selects actions uniformly at random, used for automated simulations.
 *
 * <p>
 * On each turn it picks a random {@link bugemon.common.models.bugemon.Attack} from the active Bugemon's move list, and on a forced
 * switch it picks a random available Bugemon.
 */
public class AutoStrategy implements CombatStrategy {
    private final Random random;

    /**
     * Creates an {@code AutoStrategy} backed by the given random source.
     *
     * @param random
     *            source of randomness for action selection
     */
    public AutoStrategy(Random random) {
        this.random = random;
    }

    /** {@inheritDoc} */
    @Override
    public void chooseAction(CombatContext ctx, ActionCallback callback) {
        CombatBugemon active = ctx.allyTeam().getActive();
        List<Attack> attacks = active.getAttacks();
        Attack attack = attacks.get(this.random.nextInt(attacks.size()));

        callback.onActionChosen(new TurnAction.AttackAction(attack));
    }

    /** {@inheritDoc} */
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
