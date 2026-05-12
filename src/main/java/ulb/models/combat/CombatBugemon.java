package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.effect.EffectStat;
import ulb.models.combat.effect.StatusEffect;
import ulb.models.run.RunBugemon;

public class CombatBugemon {
    private final RunBugemon runBugemon;
    private boolean participated;
    private int currentHp;
    private final List<StatusEffect> activeEffects;

    public CombatBugemon(RunBugemon runBugemon) {
        this.runBugemon = runBugemon;
        this.participated = false;
        this.currentHp = runBugemon.getCurrentHp();
        this.activeEffects = new ArrayList<>();
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

    public int getEffectiveAttack() {
        return this.runBugemon.getBaseAttack() + this.getEffectModifierSum(EffectStat.DEFENSE);
    }

    public int getEffectiveDefense() {
        return this.runBugemon.getBaseDefense() + this.getEffectModifierSum(EffectStat.DEFENSE);
    }

    public int getEffectiveInitiative() {
        return this.runBugemon.getBaseInitiative() + this.getEffectModifierSum(EffectStat.INITIATIVE);
    }

    private int getEffectModifierSum(EffectStat stat) {
        return this.activeEffects.stream().filter(e -> e.getStat() == stat).mapToInt(StatusEffect::getModifier).sum();
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
