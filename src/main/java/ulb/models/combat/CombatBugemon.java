package ulb.models.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ulb.common.StatType;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.effect.StatusEffect;
import ulb.models.run.RunBugemon;

/**
 * A combat-scoped wrapper around a {@link ulb.models.run.RunBugemon} that tracks transient battle state — current HP,
 * active {@link StatusEffect}s, and participation — without permanently modifying the underlying run data until
 * {@link #syncToRunBugemon()} is called.
 *
 * <p>
 * Effective stat accessors (e.g. {@link #getEffectiveAttack()}) sum the base stat from the underlying
 * {@code RunBugemon} with all matching effect modifiers currently active on this instance.
 */
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

    public ElementType getType() {
        return this.runBugemon.getType();
    }

    public boolean isKo() {
        return this.currentHp == 0;
    }

    public int getCurrentHp() {
        return this.currentHp;
    }

    public int getMaxHp() {
        return this.runBugemon.getMaxHp() + this.getEffectModifierSum(StatType.HP);
    }

    /**
     * Reduces current HP by {@code amount}, clamped to a minimum of zero.
     *
     * @param amount
     *            the damage to apply; must be non-negative
     */
    public void takeDamage(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    /**
     * Increases current HP by {@code amount}, clamped to {@link #getMaxHp()}. Has no effect if the Bugemon is already
     * KO.
     *
     * @param amount
     *            the HP to restore; must be non-negative
     */
    public void heal(int amount) {
        if (!this.isKo()) {
            this.currentHp = Math.min(this.getMaxHp(), this.currentHp + amount);
        }
    }

    public int getEffectiveAttack() {
        return this.runBugemon.getAttack() + this.getEffectModifierSum(StatType.ATTACK);
    }

    public int getEffectiveDefense() {
        return this.runBugemon.getDefense() + this.getEffectModifierSum(StatType.DEFENSE);
    }

    public int getEffectiveInitiative() {
        return this.runBugemon.getInitiative() + this.getEffectModifierSum(StatType.INITIATIVE);
    }

    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.runBugemon.getAttacks());
    }

    public double getXpProgress() {
        return this.runBugemon.getXpProgress();
    }

    private int getEffectModifierSum(StatType stat) {
        return this.activeEffects.stream().filter(e -> e.getStat() == stat).mapToInt(StatusEffect::getModifier).sum();
    }

    /**
     * Attaches a {@link StatusEffect} to this Bugemon.
     *
     * <p>
     * If the effect targets {@link ulb.common.StatType#HP}, the current HP is immediately adjusted by the modifier so
     * the Bugemon does not appear at a different HP ratio than the new maximum.
     *
     * @param effect
     *            the status effect to add
     */
    public void addEffect(StatusEffect effect) {
        this.activeEffects.add(effect);
        if (effect.getStat() == StatType.HP) {
            this.currentHp += effect.getModifier();
        }
    }

    /**
     * Advances each active effect by one tick and removes any that have expired. Should be called once per turn, at
     * end-of-turn.
     */
    public void tickEffects() {
        List<StatusEffect> expired = new ArrayList<>();
        this.activeEffects.forEach(effect -> {
            effect.tick();
            if (effect.isExpired()) {
                expired.add(effect);
            }
        });

        this.activeEffects.removeAll(expired);
    }

    public void clearAllEffects() {
        this.activeEffects.clear();
    }

    /**
     * Removes all negative (malus) effects from this Bugemon, leaving beneficial effects intact.
     */
    public void clearMalusEffects() {
        this.activeEffects.removeIf(StatusEffect::isNegative);
    }

    /**
     * Persists the current HP back to the underlying {@link ulb.models.run.RunBugemon}, making the combat outcome
     * durable beyond this combat session.
     */
    public void syncToRunBugemon() {
        this.runBugemon.setCurrentHp(this.currentHp);
    }

    public RunBugemon getRunBugemon() {
        return this.runBugemon;
    }

    public boolean hasAttack(Attack attack) {
        return this.getAttacks().contains(attack);
    }

    public String getSpritePath() {
        return this.runBugemon.getSpritePath();
    }

    public String getName() {
        return this.runBugemon.getName();
    }

    public int getLevel() {
        return this.runBugemon.getLevel();
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
