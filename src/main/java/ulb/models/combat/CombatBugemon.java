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

    /**
     * Wraps {@code runBugemon} for combat, initialising current HP from the run-level value and marking it as not yet
     * participated.
     *
     * @param runBugemon
     *            the underlying run Bugemon; must not be {@code null}
     */
    public CombatBugemon(RunBugemon runBugemon) {
        this.runBugemon = runBugemon;
        this.participated = false;
        this.currentHp = runBugemon.getCurrentHp();
        this.activeEffects = new ArrayList<>();
    }

    /**
     * Returns {@code true} if this Bugemon has been sent into battle at least once during the current combat.
     *
     * @return {@code true} if the Bugemon participated
     */
    public boolean hasParticipated() {
        return this.participated;
    }

    /**
     * Marks this Bugemon as having participated in the current combat, making it eligible for XP at the end of battle.
     */
    public void markAsParticipated() {
        this.participated = true;
    }

    /**
     * Returns the elemental type of this Bugemon.
     *
     * @return the {@link ElementType}
     */
    public ElementType getType() {
        return this.runBugemon.getType();
    }

    /**
     * Returns {@code true} if this Bugemon's current HP has reached zero.
     *
     * @return {@code true} when the Bugemon is knocked out
     */
    public boolean isKo() {
        return this.currentHp == 0;
    }

    /**
     * Returns the current hit points remaining for this Bugemon in the ongoing combat.
     *
     * @return current HP, in the range {@code [0, getMaxHp()]}
     */
    public int getCurrentHp() {
        return this.currentHp;
    }

    /**
     * Returns the maximum HP of this Bugemon, including any HP bonuses from active {@link StatusEffect}s.
     *
     * @return effective maximum HP
     */
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

    /**
     * Returns the effective attack stat, summing the base value with any active attack-modifying effects.
     *
     * @return effective attack
     */
    public int getEffectiveAttack() {
        return this.runBugemon.getAttack() + this.getEffectModifierSum(StatType.ATTACK);
    }

    /**
     * Returns the effective defense stat, summing the base value with any active defense-modifying effects.
     *
     * @return effective defense
     */
    public int getEffectiveDefense() {
        return this.runBugemon.getDefense() + this.getEffectModifierSum(StatType.DEFENSE);
    }

    /**
     * Returns the effective initiative stat, summing the base value with any active initiative-modifying effects.
     * Higher initiative acts first each turn.
     *
     * @return effective initiative
     */
    public int getEffectiveInitiative() {
        return this.runBugemon.getInitiative() + this.getEffectModifierSum(StatType.INITIATIVE);
    }

    /**
     * Returns an unmodifiable view of the attacks available to this Bugemon.
     *
     * @return list of available {@link Attack}s
     */
    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.runBugemon.getAttacks());
    }

    /**
     * Returns the XP progress of the underlying run Bugemon as a fraction in {@code [0.0, 1.0)}.
     *
     * @return XP progress toward the next level
     */
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

    /**
     * Removes all active {@link StatusEffect}s from this Bugemon, both positive and negative.
     */
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

    /**
     * Returns the underlying {@link RunBugemon} that this combat wrapper delegates to for base stats and identity.
     *
     * @return the wrapped run Bugemon
     */
    public RunBugemon getRunBugemon() {
        return this.runBugemon;
    }

    /**
     * Returns {@code true} if {@code attack} is in this Bugemon's move list.
     *
     * @param attack
     *            the attack to look up
     * @return {@code true} if the Bugemon knows the attack
     */
    public boolean hasAttack(Attack attack) {
        return this.getAttacks().contains(attack);
    }

    /**
     * Returns the path to this Bugemon's sprite image, delegating to the underlying run data.
     *
     * @return the sprite resource path
     */
    public String getSpritePath() {
        return this.runBugemon.getSpritePath();
    }

    /**
     * Returns the display name of this Bugemon.
     *
     * @return the Bugemon's name
     */
    public String getName() {
        return this.runBugemon.getName();
    }

    /**
     * Returns the current level of this Bugemon.
     *
     * @return the Bugemon's level
     */
    public int getLevel() {
        return this.runBugemon.getLevel();
    }

    /**
     * Two {@code CombatBugemon} instances are equal when they wrap the same {@link RunBugemon} and share the same
     * participation state.
     *
     * {@inheritDoc}
     */
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
