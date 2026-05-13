package ulb.models.bugemon;

import java.util.List;
import java.util.Objects;

public class PlayerBugemon {
    private final Bugemon base;
    private int level;
    private int xp;
    private int bonusHp;
    private int bonusAttack;
    private int bonusDefense;
    private int bonusInitiative;
    private final List<Attack> currentAttacks;

    public PlayerBugemon(Bugemon base, int level, int xp, int bonusHp, int bonusAttack, int bonusDefense,
            int bonusInitiative, List<Attack> currentAttacks) {
        this.base = Objects.requireNonNull(base);

        this.level = level;
        this.xp = xp;
        this.bonusHp = bonusHp;
        this.bonusAttack = bonusAttack;
        this.bonusDefense = bonusDefense;
        this.bonusInitiative = bonusInitiative;

        Bugemon.checkAttacks(currentAttacks);
        this.currentAttacks = currentAttacks;
    }

    public String getName() {
        return this.base.name();
    }

    public int getMaxHp() {
        return this.base.hp() + this.bonusHp;
    }

    public int getAttack() {
        return this.base.attack() + this.bonusAttack;
    }

    public int getDefense() {
        return this.base.defense() + this.bonusDefense;
    }

    public int getInitiative() {
        return this.base.initiative() + this.bonusInitiative;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PlayerBugemon other = (PlayerBugemon) obj;
        return this.base.equals(other.base) && this.level == other.level && this.xp == other.xp
                && this.bonusHp == other.bonusHp && this.bonusAttack == other.bonusAttack
                && this.bonusDefense == other.bonusDefense && this.bonusInitiative == other.bonusInitiative
                && this.currentAttacks.equals(other.currentAttacks);
    }

    @Override
    public int hashCode() {
        return this.base.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s Nv.%d XP.%d", this.base.name(), this.level, this.xp);
    }
}
