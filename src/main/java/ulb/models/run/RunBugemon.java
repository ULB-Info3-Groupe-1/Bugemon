package ulb.models.run;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.PlayerBugemon;

public class RunBugemon {
    private final PlayerBugemon playerBugemon;
    private int currentHp;

    public RunBugemon(PlayerBugemon playerBugemon, int currentHp) {
        this.playerBugemon = playerBugemon;

        Bugemon.checkHp(currentHp);
        this.currentHp = currentHp;
    }

    public String getName() {
        return this.playerBugemon.getName();
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

    public int getBaseAttack() {
        return this.playerBugemon.getBaseAttack();
    }

    public int getBaseDefense() {
        return this.playerBugemon.getBaseDefense();
    }

    public int getBaseInitiative() {
        return this.playerBugemon.getBaseInitiative();
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
