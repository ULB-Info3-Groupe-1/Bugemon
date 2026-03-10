/**
 * File name : LevelUp.java
 * Description : Class representing the level-up process for a Bugemon, including the choices
 * available to the player.
 * @author Gouverneur Martin
 * @co-author Verbeiren Lucas
 * @date 09 mar. 2026
 * @version 1.0
 */
package ulb.models.level_up;

import java.util.List;
import java.util.Random;

import ulb.common.LevelUpDTO;
import ulb.models.bugemon.Bugemon;

public class LevelUp implements LevelUpDTO {
    // Attributes
    private Bugemon bugemon;
    private List<Choice> choices;

    // Constructor

    /**
     * Constructor for the LevelUp class, initializing the Bugemon and generating random choices for
     * the level-up process.
     *
     * @param bugemon (Bugemon) the Bugemon that is leveling up
     */
    public LevelUp(Bugemon bugemon) {
        this.bugemon = bugemon;
        this.choices =
                List.of(generateRandomChoice(), generateRandomChoice(), generateRandomChoice());
    }

    /**
     * Generates a random choice of stat bonuses for the level-up process, ensuring that the total
     * points allocated across all stats equals 10.
     */
    private Choice generateRandomChoice() {
        Random rand = new Random();
        int hp = 0, attack = 0, defense = 0, initiative = 0;
        for (int i = 0; i < 10; i++) {
            int choice = rand.nextInt(4); // 0: HP, 1: Attack, 2: Defense, 3: Initiative

            switch (choice) {
                case 0 -> hp++;
                case 1 -> attack++;
                case 2 -> defense++;
                case 3 -> initiative++;
            }
        }
        return new Choice(hp * 2, attack, defense, initiative * 2);
    }

    /**
     * Gets the list of choices available to the player during the level-up process.
     */
    @Override
    public List<Choice> getChoices() {
        return this.choices;
    }

    /**
     * Gets the Bugemon that is leveling up.
     */
    @Override
    public Bugemon getBugemon() {
        return this.bugemon;
    }
}
