/**
 * File name : AutoTrainer.java
 * Description : Class representing an automatic trainer.
 *
 * @author Liefferinckx Romain
 * @date 01 March. 2026
 * @version 1.0
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
 * An {@code AutoTrainer} extends {@link Trainer} by providing two random
 * selection methods:
 * <ul>
 *   <li>{@link #getRandomAttack()} — picks a random {@link ulb.models.bugemon.Attack}
 *       from the current {@link ulb.models.bugemon.Bugemon}'s move-set.</li>
 *   <li>{@link #selectRandomBugemon()} — switches the active Bugemon to a
 *       randomly chosen alive member of the team when the current one has
 *       fainted.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is used both as the opponent in {@link ulb.models.combat.ManualCombat}
 * and as both participants in {@link ulb.models.combat.AutomaticCombat}.
 * </p>
 *
 * @see Trainer
 * @see ulb.models.combat.AutomaticCombat
 * @see ulb.models.combat.ManualCombat
 */
public class AutoTrainer extends Trainer {
    // Constructor

    /**
     * Constructor for the AutoTrainer class, initializing the team of the trainer.
     *
     * @param team (BugemonTeam) the team of the trainer, which is a list of
     *             bugemon.
     */
    public AutoTrainer(BugemonTeam team) {
        super(team);
    }

    // Methods

    /**
     * Returns a randomly selected {@link ulb.models.bugemon.Attack} from the
     * move-set of the currently active {@link ulb.models.bugemon.Bugemon}.
     *
     * <p>
     * The selection is uniformly random across all attacks known by the active
     * Bugemon. It is the caller's responsibility to ensure the current Bugemon
     * has at least one attack before calling this method.
     * </p>
     *
     * @return a randomly chosen {@link ulb.models.bugemon.Attack}; never
     *         {@code null} provided the active Bugemon's attack list is non-empty.
     * @throws IllegalArgumentException if the active Bugemon has no attacks
     *         (empty list passed to {@link java.util.Random#nextInt(int)}).
     */
    public Attack getRandomAttack() {
        Random rand = new Random();
        List<Attack> attacks = this.currentBugemon.getAttackList();
        int attackIndex = rand.nextInt(attacks.size());
        return attacks.get(attackIndex);
    }

    /**
     * Switches the active {@link ulb.models.bugemon.Bugemon} to a randomly
     * chosen alive member of this trainer's team.
     *
     * <p>
     * If the trainer is already {@link #isDefeated() defeated} (i.e., all
     * Bugemons have fainted), this method returns immediately without modifying
     * {@code currentBugemon}.
     * </p>
     *
     * <p>
     * Only Bugemons for which {@link ulb.models.bugemon.Bugemon#isAlive()}
     * returns {@code true} are considered as candidates. The selection is
     * uniformly random among those candidates.
     * </p>
     *
     * @throws IllegalArgumentException if there are no alive Bugemons left and
     *         {@link #isDefeated()} was not caught (should not happen in normal
     *         game flow).
     */
    public void selectRandomBugemon() {
        if (isDefeated()) {
            return;
        }

        List<Bugemon> aliveBugemons = this.team.stream().filter(Bugemon::isAlive).toList();

        Random rand = new Random();
        int randomBugemonIndex = rand.nextInt(aliveBugemons.size());
        this.currentBugemon = aliveBugemons.get(randomBugemonIndex);
    }
}
