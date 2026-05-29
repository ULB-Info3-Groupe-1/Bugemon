package bugemon.common.models.player;

import java.io.Serializable;

/**
 * Stores additive stat bonuses (HP, attack, defense, initiative) accumulated by the player through skill rewards and
 * tower progression.
 *
 * <p>
 * Bonuses are applied on top of a Bugemon's base stats at the start of a run. The no-arg constructor (package private)
 * creates a zero-bonus instance used as a default.
 */
public class BonusStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private int bonusHp;
    private int bonusAttack;
    private int bonusDefense;
    private int bonusInitiative;

    BonusStats() {
        this(0, 0, 0, 0);
    }

    public BonusStats(int bonusHp, int bonusAttack, int bonusDefense, int bonusInitiative) {
        this.bonusHp = bonusHp;
        this.bonusAttack = bonusAttack;
        this.bonusDefense = bonusDefense;
        this.bonusInitiative = bonusInitiative;
    }

    public int getBonusHp() {
        return this.bonusHp;
    }

    public int getBonusAttack() {
        return this.bonusAttack;
    }

    public int getBonusDefense() {
        return this.bonusDefense;
    }

    public int getBonusInitiative() {
        return this.bonusInitiative;
    }

    public void setBonusHp(int bonusHp) {
        this.bonusHp = bonusHp;
    }

    public void setBonusAttack(int bonusAttack) {
        this.bonusAttack = bonusAttack;
    }

    public void setBonusDefense(int bonusDefense) {
        this.bonusDefense = bonusDefense;
    }

    public void setBonusInitiative(int bonusInitiative) {
        this.bonusInitiative = bonusInitiative;
    }

    /**
     * Adds every field of {@code bonusStats} to the corresponding field of this instance.
     *
     * @param bonusStats
     *            the bonus increments to merge into this object
     */
    public void add(BonusStats bonusStats) {
        this.bonusHp += bonusStats.getBonusHp();
        this.bonusAttack += bonusStats.getBonusAttack();
        this.bonusDefense += bonusStats.getBonusDefense();
        this.bonusInitiative += bonusStats.getBonusInitiative();
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.bonusHp, this.bonusAttack, this.bonusDefense, this.bonusInitiative);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BonusStats other = (BonusStats) obj;
        return this.bonusHp == other.bonusHp && this.bonusAttack == other.bonusAttack
                && this.bonusDefense == other.bonusDefense && this.bonusInitiative == other.bonusInitiative;
    }
}
