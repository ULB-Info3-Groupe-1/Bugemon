/**
 * File name : ManualTrainer.java
 * Description : Class representing a manual trainer.
 * 
 * @author Liefferinckx Romain
 * @date 02 March. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * This class represents a manual trainer, which has a team of bugemon and can
 * select an action during a combat.
 */
public class ManualTrainer extends Trainer {

    // Enums

    /**
     * Enum representing the possible actions of a trainer during a combat: attack,
     * switch, or forfeit.
     */
    public enum TAction {
        ATTACK, SWITCH, FORFEIT
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
        for (Bugemon b : team.getTeam()) {
            if (b.equals(bugemon) && b.isAlive()) {
                this.selectedBugemon = b;
                return;
            }
        }
    }

    /**
     * Select an attack from the list of attacks of the current bugemon of the
     * trainer
     * 
     * @param attack (Attack) the attack to select from the list of attacks of the
     *               current bugemon of the trainer.
     */
    public void selectAttack(Attack attack) {
        if (getCurrentBugemonAttackList().contains(attack)) {
            this.selectedAttack = attack;
        }
    }

    /**
     * Select an action for the trainer during a combat.
     *
     * @return (int) the power of the attack if it is in the list of attacks of the
     *         current bugemon of the trainer, 0 otherwise.
     */
    public int getAttackPower() {
        if (this.selectedAttack != null && getCurrentBugemonAttackList().contains(this.selectedAttack)) {
            return this.selectedAttack.getPower();
        }
        return 0;
    }

    // Getters and setters

    /**
     * Returns the selected action for the trainer during a combat.
     * 
     * @return (TAction) the selected action for the trainer during a combat.
     */
    public TAction getSelectedAction() {
        return selectedAction;
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
        return selectedAttack;
    }

    /**
     * Returns the selected bugemon for the trainer during a combat.
     * 
     * @return (Bugemon) the selected bugemon for the trainer during a combat.
     */
    public Bugemon getSelectedBugemon() {
        return selectedBugemon;
    }

    /**
     * Select a bugemon for the trainer during a combat.
     * 
     * @param selectedBugemon (Bugemon) the bugemon to select for the trainer during
     *                        a combat.
     */
    public void setSelectedBugemon(Bugemon selectedBugemon) {
        this.selectedBugemon = selectedBugemon;
    }
}
