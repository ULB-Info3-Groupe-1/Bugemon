/**
 * File name : AutoTrainer.java
 * Description : Class representing an automatic trainer.
 * 
 * @author Liefferinckx Romain
 * @date 01 March. 2026
 * @version 1.0
 */

package ulb.models.trainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * This class represents an automatic trainer, which has a team of bugemon and
 * can
 * select a random action during a combat.
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
     * Choose a random attack from the list of attacks of the current bugemon of the
     * trainer and return its power.
     * 
     * @return (int) a random attack power from the list of attacks of the current
     *         bugemon of the trainer.
     */
    public int getRandomAttack() {
        Random rand = new Random();
        AttackList attacks = getCurrentBugemonAttackList();
        int attackIndex = rand.nextInt(attacks.getAttacks().size());
        return attacks.get(attackIndex).getPower();
    }

    /**
     * Choose a random bugemon from the team of the trainer.
     * 
     * @return (Bugemon) a random bugemon from the team of the trainer.
     */
    public void selectRandomBugemon() {
        if (isDefeated()) {
            return;
        }
        List<Bugemon> aliveBugemons = new ArrayList<>();
        for (Bugemon b : team.getTeam()) {
            if (b.isAlive()) {
                aliveBugemons.add(b);
            }
        }
        Random rand = new Random();
        int randomBugemonIndex = rand.nextInt(aliveBugemons.size());
        this.currentBugemon = aliveBugemons.get(randomBugemonIndex);
    }

    /**
     * Returns the size of the team of the trainer.
     * 
     * @return (int) the size of the team of the trainer.
     */
    public int getTeamSize() {
        return team.size();
    }
}
