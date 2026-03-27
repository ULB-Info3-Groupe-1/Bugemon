/**
 * File name: AutoTrainer.java
 * Description: Class representing an automatic trainer.
 *
 * @author Liefferinckx Romain
 * @date 01 March 2026
 * @version 2.0
 */

package ulb.models.trainer;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Represents an AI-controlled trainer that selects actions automatically
 * during combat by choosing at random from available options.
 *
 * <p>
 * {@code AutoTrainer} extends {@link Trainer} by implementing the two abstract
 * strategy methods of the trainer contract:
 * <ul>
 *   <li>{@link #getAction()} — always returns an {@link TurnAction.AttackAction}
 *       wrapping a randomly chosen {@link Attack} from the active
 *       {@link Bugemon}'s move-set.</li>
 *   <li>{@link #reactToKo()} — switches the active {@link Bugemon} to a
 *       randomly chosen alive member of the team when the current one faints.</li>
 * </ul>
 *
 * <p>
 * This class is used both as the opponent side in a player-vs-AI combat and
 * as both participants in a fully automated combat session.
 * </p>
 *
 * @see Trainer
 * @see TurnAction
 * @see ulb.models.combat.Combat
 */
public class AutoTrainer extends Trainer {
    private static final Random RAND = new Random();

    /**
     * Constructs an {@code AutoTrainer} with the given team.
     *
     * @param team the {@link BugemonTeam} assigned to this trainer;
     *             must not be {@code null} and must contain at least one
     *             {@link Bugemon}.
     */
    public AutoTrainer(BugemonTeam team) {
        super(team);
    }

    // ── Trainer contract ──────────────────────────────────────────────────────

    @Override
    public boolean isBot() {
        return true;
    }

    /**
     * Decides the action for this turn by selecting a random attack from the
     * current {@link Bugemon}'s move-set.
     *
     * <p>
     * Always returns a {@link TurnAction.AttackAction}; an {@code AutoTrainer}
     * never voluntarily switches or forfeits.
     * </p>
     *
     * @return a {@link TurnAction.AttackAction} wrapping a randomly chosen
     *         {@link Attack}; never {@code null}.
     * @throws IllegalArgumentException if the active Bugemon has no attacks.
     */
    @Override
    public TurnAction getAction() {
        return new TurnAction.AttackAction(getRandomAttack());
    }

    /**
     * Reacts to the current {@link Bugemon} fainting by switching to a randomly
     * chosen alive member of the team.
     *
     * <p>
     * Delegates to {@link #selectRandomBugemon()}. If the trainer is already
     * fully {@link #isDefeated() defeated}, this method returns immediately
     * without modifying {@code currentBugemon}.
     * </p>
     */
    @Override
    public void reactToKo() {
        selectRandomBugemon();
    }

    // ── Public helpers ────────────────────────────────────────────────────────

    /**
     * Returns a randomly selected {@link Attack} from the move-set of the
     * currently active {@link Bugemon}.
     *
     * <p>
     * The selection is uniformly random across all attacks known by the active
     * Bugemon. The caller must ensure the current Bugemon has at least one
     * attack before invoking this method.
     * </p>
     *
     * @return a randomly chosen {@link Attack}; never {@code null} provided the
     *         active Bugemon's attack list is non-empty.
     * @throws IllegalArgumentException if the active Bugemon has no attacks
     *         (empty list passed to {@link Random#nextInt(int)}).
     */
    public Attack getRandomAttack() {
        List<Attack> attacks = currentBugemon.getAttackList();
        return attacks.get(RAND.nextInt(attacks.size()));
    }

    /**
     * Switches the active {@link Bugemon} to a randomly chosen alive member of
     * this trainer's team.
     *
     * <p>
     * If the trainer is already {@link #isDefeated() defeated} (i.e., all
     * Bugemons have fainted), this method returns immediately without modifying
     * {@code currentBugemon}.
     * </p>
     *
     * <p>
     * Only Bugemons for which {@link Bugemon#isAlive()} returns {@code true}
     * are considered as candidates. The selection is uniformly random among
     * those candidates.
     * </p>
     */
    public void selectRandomBugemon() {
        if (isDefeated()) {
            return;
        }

        List<Bugemon> aliveBugemons = this.team.aliveStream().toList();
        currentBugemon = aliveBugemons.get(RAND.nextInt(aliveBugemons.size()));
    }
}
