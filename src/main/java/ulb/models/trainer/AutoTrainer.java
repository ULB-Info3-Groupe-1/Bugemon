package ulb.models.trainer;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.Team;

/**
 * AI-controlled trainer that always attacks at random and switches to a random alive team member on KO. Used both as
 * the opponent in player-vs-AI combat and as both participants in a fully automated session.
 *
 * @see Trainer
 * @see TurnAction
 */
public class AutoTrainer extends Trainer {
    private static final Random RAND = new Random();

    public AutoTrainer(Team team) {
        super(team);
    }

    // ── Trainer contract ──────────────────────────────────────────────────────

    /**
     * Always returns a {@link TurnAction.AttackAction} wrapping a randomly chosen attack; never switches or forfeits.
     *
     * @throws IllegalArgumentException
     *             if the active Bugemon has no attacks.
     */
    @Override
    public TurnAction getAction() {
        return new TurnAction.AttackAction(this.getRandomAttack());
    }

    /** Delegates to {@link #selectRandomBugemon()}; no-op if the trainer is already {@link #isDefeated() defeated}. */
    @Override
    public void reactToKo() {
        this.selectRandomBugemon();
    }

    // ── Public helpers ────────────────────────────────────────────────────────

    /**
     * Returns a uniformly random {@link Attack} from the active Bugemon's move-set.
     *
     * @throws IllegalArgumentException
     *             if the active Bugemon has no attacks.
     */
    public Attack getRandomAttack() {
        List<Attack> attacks = currentBugemon.getAttackList();
        return attacks.get(RAND.nextInt(attacks.size()));
    }

    /**
     * Switches the active Bugemon to a uniformly random alive team member. No-op if the trainer is already
     * {@link #isDefeated() defeated}.
     */
    public void selectRandomBugemon() {
        if (isDefeated()) {
            return;
        }

        List<Bugemon> aliveBugemons = this.team.aliveStream().toList();
        currentBugemon = aliveBugemons.get(RAND.nextInt(aliveBugemons.size()));
    }
}
