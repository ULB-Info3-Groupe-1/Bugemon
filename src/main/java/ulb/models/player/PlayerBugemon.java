package ulb.models.player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.level_up.Upgrade;
import ulb.repositories.dto.PlayerBugemonDTO;

public class PlayerBugemon {
    private final Bugemon base;
    private int level;
    private int xp;
    private BonusStats bonusStats;
    private final List<Attack> currentAttacks;

    public PlayerBugemon(Bugemon base) {
        this(base, 1, 0, new BonusStats(), base.attacks());
    }

    public static PlayerBugemon from(Bugemon base, PlayerBugemonDTO dto) {
        BonusStats bonusStats = new BonusStats(
                dto.bonusMaxHp(), dto.bonusAttackPower(), dto.bonusDefense(), dto.bonusInitiative());
        return new PlayerBugemon(base, dto.level(), dto.xp(), bonusStats, base.attacks());
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

    public ElementType getType() {
        return this.base.type();
    }

    public int getMaxHp() {
        return this.base.hp() + this.bonusStats.getBonusHp();
    }

    public int getXp() {
        return this.xp;
    }

    public int getLevel() {
        return this.level;
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

    public int getXpToNextLevel() {
        return 50 + 50 * (this.level - 1);
    }

    public int addXp(int xpToAdd) {
        if (xpToAdd < 0) {
            throw new IllegalArgumentException("amount of xp to add must be non-negative");
        }

        int numLevelUps = 0;

        this.xp += xpToAdd;

        while (this.xp >= this.getXpToNextLevel()) {
            this.xp -= this.getXpToNextLevel();
            numLevelUps++;
            this.level++;
        }

        return numLevelUps;
    }

    public void applyUpgrade(Upgrade upgrade) {
        this.bonusStats.apply(upgrade);
    }

    public String getSpritePath() {
        return this.base.spritePath();
    }

    public List<Attack> getAttackList() {
        return this.currentAttacks;
    }

    public double getXpProgress() {
        return (double) this.xp / this.getXpToNextLevel();
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

    public PlayerBugemonDTO toDTO(String playerName) {
        return new PlayerBugemonDTO(playerName, this.base.name(), this.bonusStats.getBonusDefense(),
                this.bonusStats.getBonusAttack(), this.bonusStats.getBonusInitiative(),
                this.bonusStats.getBonusHp(), this.xp, this.level);
    }
}
