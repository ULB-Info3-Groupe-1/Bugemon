package ulb.models.run;

import java.util.Collections;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.player.BonusStats;
import ulb.models.player.PlayerBugemon;

public class RunBugemon {
    private final PlayerBugemon playerBugemon;
    private int currentHp;

    /**
     * Creates a fresh RunBugemon from a PlayerBugemon (hp full).
     */
    public RunBugemon(PlayerBugemon playerBugemon) {
        this(playerBugemon, playerBugemon.getMaxHp());
    }

    public RunBugemon(PlayerBugemon playerBugemon, int currentHp) {
        this.playerBugemon = playerBugemon;

        Bugemon.checkHp(currentHp);
        this.currentHp = currentHp;
    }

    public String getName() {
        return this.playerBugemon.getName();
    }

    public ElementType getType() {
        return this.playerBugemon.getType();
    }

    public boolean isKo() {
        return this.currentHp == 0;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return this.playerBugemon.getMaxHp();
    }

    public void restoreHpToMax() {
        this.currentHp = this.getMaxHp();
    }

    public int getAttack() {
        return this.playerBugemon.getAttack();
    }

    public int getDefense() {
        return this.playerBugemon.getDefense();
    }

    public int getInitiative() {
        return this.playerBugemon.getInitiative();
    }

    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.playerBugemon.getAttacks());
    }

    public double getXpProgress() {
        return this.playerBugemon.getXpProgress();
    }

    public void applyUpgrade(BonusStats bonus) {
        this.playerBugemon.applyUpgrade(bonus);
        this.currentHp = this.getMaxHp();
    }

    public String getSpritePath() {
        return this.playerBugemon.getSpritePath();
    }

    public int getLevel() {
        return this.playerBugemon.getLevel();
    }

    public PlayerBugemon getPlayerBugemon() {
        return this.playerBugemon;
    }

    public int addXp(int xp) {
        return this.playerBugemon.addXp(xp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RunBugemon other = (RunBugemon) obj;
        return this.playerBugemon.equals(other.playerBugemon) && (this.currentHp == other.currentHp);
    }

    @Override
    public int hashCode() {
        return this.playerBugemon.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s CURRENT-HP:%d", this.getName(), this.currentHp);
    }
}
