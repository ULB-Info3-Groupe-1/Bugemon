/**
 * File name : Choice.java
 * Description : Data class representing a choice of stat bonuses that a player can select during
 * the level-up process of a Bugemon.
 * @author Gouverneur Martin
 * @co-author Verbeiren Lucas
 * @date 09 mar. 2026
 * @version 1.0
 */
package ulb.models.level_up;

/**
 * Immutable data object representing one stat-bonus option offered to the
 * player during a {@link LevelUp}.
 *
 * <p>
 * Each {@code Upgrade} bundles four non-negative bonus values — one per
 * combat stat
 * </p>
 *
 * <p>
 * Instances are created by {@link LevelUp#generateRandomChoice()} and are
 * never modified after construction.
 * </p>
 *
 * @see LevelUp
 * @see ulb.models.bugemon.Bugemon#applyChoice(Choice)
 */
public record Upgrade(

        int hp,

        int attack,

        int defense,

        int initiative

) {
    /**
     * Returns a human-readable summary of this choice's bonuses, suitable for
     * display in the level-up screen.
     *
     * @return a formatted string of the form
     *         {@code "+N HP +N Attack +N Defense +N Initiative"}.
     */
    public String toString() {
        return String.format("+%d HP +%d Attack +%d Defense +%d Initiative", hp, attack, defense,
                             initiative);
    }
}
