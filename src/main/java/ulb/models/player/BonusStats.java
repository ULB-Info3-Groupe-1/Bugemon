package ulb.models.player;

import ulb.models.level_up.Upgrade;

public class BonusStats {
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

    public void apply(Upgrade upgrade) {
        this.bonusHp += upgrade.hp();
        this.bonusAttack += upgrade.attack();
        this.bonusDefense += upgrade.defense();
        this.bonusInitiative += upgrade.initiative();
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
