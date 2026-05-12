package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;

import ulb.models.combat.effect.StatusEffect;
import ulb.models.run.RunBugemon;

public class CombatBugemon {
    private final RunBugemon runBugemon;
    private boolean participated;
    private int currentHp;
    private final List<StatusEffect> effects;

    public CombatBugemon(RunBugemon runBugemon) {
        this.runBugemon = runBugemon;
        this.participated = false;
        this.currentHp = runBugemon.getCurrentHp();
        this.effects = new ArrayList<>();
    }

    public boolean hasParticipated() {
        return this.participated;
    }

    public void markAsParticipated() {
        this.participated = true;
    }

    public boolean isKo() {
        return this.currentHp == 0;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    public int getMaxHp() {
        return this.runBugemon.getMaxHp();
    }

    public void takeDamage(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void heal(int amount) {
        if (!this.isKo()) {
          this.currentHp = Math.min(this.getMaxHp(), this.currentHp + amount);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CombatBugemon other = (CombatBugemon) obj;
        return this.runBugemon.equals(other.runBugemon) && (this.participated == other.participated);
    }

    @Override
    public int hashCode() {
        return this.runBugemon.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s PARTICIPATED:%b", this.runBugemon.getName(), this.participated);
    }
}
