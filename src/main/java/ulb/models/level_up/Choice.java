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
 * Each {@code Choice} bundles four non-negative bonus values — one per
 * combat stat — that will be added to a {@link ulb.models.bugemon.Bugemon}'s
 * current state when the player selects it via
 * {@link ulb.models.bugemon.Bugemon#applyChoice(Choice)}.
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
public class Choice {
    int bonusHP, bonusAttack, bonusDefense, bonusInitiative;

    /**
     * Constructs a {@code Choice} with the given stat bonuses.
     *
     * @param bonusHP         the bonus to add to the bugemon's HP; must be
     *                        non-negative.
     * @param bonusAttack     the bonus to add to the bugemon's attack stat;
     *                        must be non-negative.
     * @param bonusDefense    the bonus to add to the bugemon's defense stat;
     *                        must be non-negative.
     * @param bonusInitiative the bonus to add to the bugemon's initiative stat;
     *                        must be non-negative.
     */
    public Choice(int bonusHP, int bonusAttack, int bonusDefense, int bonusInitiative) {
        this.bonusHP = bonusHP;
        this.bonusAttack = bonusAttack;
        this.bonusDefense = bonusDefense;
        this.bonusInitiative = bonusInitiative;
    }

    /**
     * Returns the HP bonus provided by this choice.
     *
     * @return the non-negative HP bonus.
     */
    public int getBonusHP() {
        return bonusHP;
    }

    /**
     * Returns the attack bonus provided by this choice.
     *
     * @return the non-negative attack bonus.
     */
    public int getBonusAttack() {
        return bonusAttack;
    }

    /**
     * Returns the defense bonus provided by this choice.
     *
     * @return the non-negative defense bonus.
     */
    public int getBonusDefense() {
        return bonusDefense;
    }

    /**
     * Returns the initiative bonus provided by this choice.
     *
     * @return the non-negative initiative bonus.
     */
    public int getBonusInitiative() {
        return bonusInitiative;
    }

    /**
     * Returns a human-readable summary of this choice's bonuses, suitable for
     * display in the level-up screen.
     *
     * @return a formatted string of the form
     *         {@code "+N HP +N Attack +N Defense +N Initiative"}.
     */
    public String toString() {
        return String.format("+%d HP +%d Attack +%d Defense +%d Initiative", bonusHP, bonusAttack,
                             bonusDefense, bonusInitiative);
    }
}
