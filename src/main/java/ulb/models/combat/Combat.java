/**
 * File name : Combat.java
 * Description : Class representing a combat between two trainers.
 *
 * @author Liefferinckx Romain
 * @date 26 feb. 2026
 * @version 1.0
 */

package ulb.models.combat;

import java.io.IOException;
import java.security.KeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon_team.BugemonTeam;

public class Combat {

    // Attributes

    protected Trainer allyTrainer; // allied trainer
    protected Trainer adversaryTrainer; // adversary trainer
    protected int turn;

    protected Map<String, Map<Integer, Effect>> allyEffects = new HashMap<>();
    protected Map<String, Effect> adversaryEffects = new HashMap<>();

    // Constructor

    /**
     * Constructor for the Combat class, initializing the two trainers and the turn
     * of the combat.
     */
    public Combat(Trainer allyTrainer, Trainer adversaryTrainer) {
        this.allyTrainer = allyTrainer;
        this.adversaryTrainer = adversaryTrainer;
        this.turn = 0;
    }

    // Methods

    /**
     * 
     * @return
     */
    public Trainer turn() {
        // applyAttack();
        Trainer winner = getWinner();
        if (winner != null) {
            return winner;
        }
        nextTurn();
        return null;
    }

    /**
     * Returns the winner of the combat if it is over, null otherwise.
     */
    public Trainer getWinner() {
        if (isFinished()) {
            if (allyTrainer.isDefeated()) {
                return adversaryTrainer;
            } else {
                return allyTrainer;
            }
        }
        return null;
    }

    public boolean isFinished() {
        if (allyTrainer.isDefeated() || adversaryTrainer.isDefeated()) {
            return true;
        }
        return false;
    }

    public void incrementTurn() {
        this.turn++;
    }

    // Getters and setters

    /**
     * Returns the turn of the combat.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Returns the ally trainer.
     */
    public Trainer getAllyTrainer() {
        return this.allyTrainer;
    }

    /**
     * Returns the adversary trainer.
     */
    public Trainer getAdversaryTrainer() {
        return this.adversaryTrainer;
    }

    protected void applyAttack(Trainer attacker, Trainer defender, Attack attack) throws KeyException {
        List<Effect> effects = attack.getEffects();

        for (Effect e : effects) {
            String target = e.getTarget();

            switch (target) {
                case "adversaire":
                    handleEffect(defender.getCurrentBugemon(), e);
                    break;
                case "lanceur":
                    handleEffect(attacker.getCurrentBugemon(), e);
                    break;
                case "equipe":
                    BugemonTeam team = attacker.getTeam();
                    for (Bugemon bugemon : team.getTeam()) {
                        handleEffect(bugemon, e);
                    }
                    break;
                default:
                    throw new KeyException("Invalid effect target");
            }
        }
    }

    private void handleEffect(Bugemon bugemon, Effect effect) {
        int value = effect.getModifier();
        String stat = effect.getStat();
        try {
            bugemon.editStat(stat, value);
        } catch (Exception e) {
            // TODO: handle exception -> Logger ?
            System.err.println(e.getStackTrace());
        }

    }
}
