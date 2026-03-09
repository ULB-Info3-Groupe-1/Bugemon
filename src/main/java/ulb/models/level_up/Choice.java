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

public class Choice {
    int bonusHP, bonusAttack, bonusDefense, bonusInitiative;

    public Choice(int bonusHP, int bonusAttack, int bonusDefense, int bonusInitiative) {
        this.bonusHP = bonusHP;
        this.bonusAttack = bonusAttack;
        this.bonusDefense = bonusDefense;
        this.bonusInitiative = bonusInitiative;
    }

    public int getBonusHP() {
        return bonusHP;
    }

    public int getBonusAttack() {
        return bonusAttack;
    }

    public int getBonusDefense() {
        return bonusDefense;
    }

    public int getBonusInitiative() {
        return bonusInitiative;
    }

    public String toString() {
        return String.format("+%d HP +%d Attack +%d Defense +%d Initiative", bonusHP, bonusAttack,
                             bonusDefense, bonusInitiative);
    }
}