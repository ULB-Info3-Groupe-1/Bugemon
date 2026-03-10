/**
 * File name : ManualTrainer.java
 * Description : Class representing a manual trainer.
 *
 * @author Liefferinckx Romain
 * @date 02 March. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * Represents a human-controlled trainer who selects actions explicitly during
 * combat.
 *
 * <p>
 * A {@code ManualTrainer} extends {@link Trainer} by adding the concept of a
 * <em>selected action</em>: before each turn the player (or the controller
 * layer) must call {@link #selectAction(TAction)} to choose whether to attack,
 * switch the active Bugemon, or forfeit. Supporting selection methods
 * ({@link #selectAttack}, {@link #selectBugemon}) let the player further
 * specify the details of the chosen action.
 * </p>
 *
 * <p>
 * All selection state is held locally and is not cleared automatically between
 * turns; the controller is responsible for resetting or overwriting it as
 * needed.
 * </p>
 *
 * @see AutoTrainer
 * @see ulb.models.combat.ManualCombat
 * @see TAction
 */
public class ManualTrainer extends Trainer {
    // Enums

    /**
     * Enumerates the actions a {@link ManualTrainer} can take during a combat turn.
     *
     * <p>
     * The selected action is passed to
     * {@link ulb.models.combat.ManualCombat#turn(TAction)} to drive the turn
     * resolution logic:
     * <ul>
     *   <li>{@link #ATTACK}  — use the currently {@link ManualTrainer#selectAttack
     *       selected attack} against the opponent.</li>
     *   <li>{@link #SWITCH}  — swap the active Bugemon for the one set via
     *       {@link ManualTrainer#selectBugemon}.</li>
     *   <li>{@link #FORFEIT} — immediately concede the match; the opponent is
     *       declared the winner.</li>
     * </ul>
     * </p>
     */
    public enum TAction {
        /** Use the currently selected {@link Attack} against the opponent. */
        ATTACK,

        /**
         * Switch the active {@link Bugemon} for the one chosen via
         * {@link ManualTrainer#selectBugemon}.
         */
        SWITCH,

        /** Concede the match; the opponent wins immediately. */
        FORFEIT,
    }

    // Attributes

    private TAction selectedAction;
    private Attack selectedAttack;
    private Bugemon selectedBugemon;

    // Constructor

    /**
     * Constructor for the ManualTrainer class, initializing the team of the
     * trainer.
     *
     * @param team (BugemonTeam) the team of the trainer, which is a list of
     *             bugemon.
     */
    public ManualTrainer(BugemonTeam team) {
        super(team);
        this.selectedAction = null;
        this.selectedAttack = null;
        this.selectedBugemon = null;
    }

    // Methods

    /**
     * Select a bugemon from the team of the trainer if it is alive.
     *
     * @param bugemon (Bugemon) the bugemon to select from the team of the trainer.
     */
    public void selectBugemon(Bugemon bugemon) {
        if (this.isDefeated()) {
            return;
        }

        Bugemon found = this.team.stream()
                                .filter(b -> b.equals(bugemon))
                                .findFirst()
                                .orElseThrow(()
                                                     -> new IllegalArgumentException(
                                                             "The selected bugemon is not in the "
                                                             + "team of the trainer."));

        if (!found.isAlive()) {
            throw new IllegalArgumentException("The selected bugemon is not alive.");
        }

        this.selectedBugemon = found;
    }

    /**
     * Select an attack from the list of attacks of the current bugemon of the
     * trainer
     *
     * @param attack (Attack) the attack to select from the list of attacks of the
     *               current bugemon of the trainer.
     */
    public void selectAttack(Attack attack) {
        if (this.currentBugemonContainsAttack(attack)) {
            this.selectedAttack = attack;
        } else {
            throw new IllegalArgumentException("The selected attack is not in the list of attacks "
                                               + "of the current bugemon of the trainer.");
        }
    }

    /**
     * Returns the base power of the currently selected {@link Attack}.
     *
     * <p>
     * This is a convenience accessor used by the combat layer to determine
     * how much damage the trainer's chosen attack will deal before modifiers
     * are applied.
     * </p>
     *
     * @return the {@link Attack#getPower() power} of the selected attack, or
     *         {@code 0} if no attack has been selected yet.
     */
    public int getAttackPower() {
        if (this.selectedAttack == null) {
            return 0;
        }
        return this.selectedAttack.getPower();
    }

    // Getters and setters

    /**
     * Returns the selected action for the trainer during a combat.
     *
     * @return (TAction) the selected action for the trainer during a combat.
     */
    public TAction getSelectedAction() {
        return this.selectedAction;
    }

    /**
     * Select an action for the trainer during a combat.
     *
     * @param action (TAction) the action to select for the trainer during a combat.
     */
    public void selectAction(TAction action) {
        this.selectedAction = action;
    }

    /**
     * Returns the selected attack for the trainer during a combat.
     *
     * @return (Attack) the selected attack for the trainer during a combat.
     */
    public Attack getSelectedAttack() {
        return this.selectedAttack;
    }

    /**
     * Returns the selected bugemon for the trainer during a combat.
     *
     * @return (Bugemon) the selected bugemon for the trainer during a combat.
     */
    public Bugemon getSelectedBugemon() {
        return this.selectedBugemon;
    }

    /**
     * Directly sets the Bugemon that will be switched in on the next
     * {@link TAction#SWITCH} turn, bypassing the validation performed by
     * {@link #selectBugemon(Bugemon)}.
     *
     * <p>
     * This setter is primarily intended for use by the controller layer when
     * it needs to programmatically pre-assign a switch target without triggering
     * the alive-check guard in {@link #selectBugemon}.
     * </p>
     *
     * @param selectedBugemon the {@link Bugemon} to set as the switch target;
     *                        may be {@code null} to clear a previous selection.
     */
    public void setSelectedBugemon(Bugemon selectedBugemon) {
        this.selectedBugemon = selectedBugemon;
    }
}
