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

import ulb.common.dto.LevelUpDTO;
import ulb.models.bugemon.Bugemon;

public class LevelUp implements LevelUpDTO {
    private static final Random RANDOM = new Random();

    // Attributes
    private Bugemon bugemon;
    private List<Upgrade> choices;

    // Constructor

    /**
     * Constructs a {@code LevelUp} for the given {@link Bugemon}, automatically generating three
     * random stat-bonus {@link Upgrade}s for the player to select from.
     *
     * @param bugemon
     *            the {@link Bugemon} that is levelling up; must not be {@code null}.
     */
    public LevelUp(Bugemon bugemon) {
        this.bugemon = bugemon;
        this.choices = List.of(this.generateRandomChoice(), this.generateRandomChoice(),
                this.generateRandomChoice());
    }

    /**
     * Generates a single random {@link Upgrade} of stat bonuses for the level-up process.
     *
     * <p>
     * A total of 10 points are distributed randomly across the four stats ({@code HP},
     * {@code Attack}, {@code Defense}, {@code Initiative}). Each point is independently assigned to
     * one of the four stats with equal probability. The raw point counts are then scaled before
     * being passed to the {@link Upgrade} constructor:
     * </p>
     * <ul>
     * <li><strong>HP</strong> and <strong>Initiative</strong> are multiplied by {@code 2}, so each
     * can yield between {@code 0} and {@code 20} bonus points.</li>
     * <li><strong>Attack</strong> and <strong>Defense</strong> are kept at face value, so each can
     * yield between {@code 0} and {@code 10} bonus points.</li>
     * </ul>
     *
     * @return a new {@link Upgrade} whose four bonus values sum to at most {@code 60} (all 10
     *         points on HP or Initiative at 2× weight).
     */
    private Upgrade generateRandomChoice() {
        int hp = 0;
        int attack = 0;
        int defense = 0;
        int initiative = 0;

        for (int i = 0; i < 10; i++) {
            int choice = RANDOM.nextInt(4); // 0: HP, 1: Attack, 2: Defense, 3: Initiative

            switch (choice) {
                case 0 -> hp++;
                case 1 -> attack++;
                case 2 -> defense++;
                case 3 -> initiative++;
                default -> throw new IllegalStateException("Unexpected value: " + choice);
            }
        }
        return new Upgrade(hp * 2, attack, defense, initiative * 2);
    }

    /**
     * Returns the list of stat-bonus choices available to the player during the level-up process.
     *
     * @return an unmodifiable {@link List} of exactly three {@link Upgrade} instances generated at
     *         construction time; never {@code null}.
     */
    @Override
    public List<Upgrade> getChoices() {
        return this.choices;
    }

    /**
     * Returns the {@link Bugemon} that triggered this level-up.
     *
     * @return the levelling-up {@link Bugemon}; never {@code null}.
     */
    @Override
    public Bugemon getBugemon() {
        return this.bugemon;
    }
}
