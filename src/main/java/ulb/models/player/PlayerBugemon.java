package ulb.models.player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;

public class PlayerBugemon {
    private final Bugemon base;
    private int level;
    private int xp;
    private BonusStats bonusStats;
    private final List<Attack> currentAttacks;

    public PlayerBugemon(Bugemon base) {
        this(base, 1, 0, new BonusStats(), base.attacks());
    }

    public PlayerBugemon(Bugemon base, int level, int xp, BonusStats bonusStats, List<Attack> currentAttacks) {
        this.base = Objects.requireNonNull(base);

        this.level = level;
        this.xp = xp;
        this.bonusStats = bonusStats;

        Bugemon.checkAttacks(currentAttacks);
        this.currentAttacks = currentAttacks;
    }

    public String getName() {
        return this.base.name();
    }

    public int getMaxHp() {
        return this.base.hp() + this.bonusStats.getBonusHp();
    }

    public int getAttack() {
        return this.base.attack() + this.bonusStats.getBonusAttack();
    }

    public int getDefense() {
        return this.base.defense() + this.bonusStats.getBonusDefense();
    }

    public int getInitiative() {
        return this.base.initiative() + this.bonusStats.getBonusInitiative();
    }

    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.currentAttacks);
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
                && this.bonusStats.equals(other.bonusStats) && this.currentAttacks.equals(other.currentAttacks);
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
