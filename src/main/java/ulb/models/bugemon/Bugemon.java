package ulb.models.bugemon;

import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.components.AttackComponent;
import ulb.models.bugemon.components.DefenseComponent;
import ulb.models.bugemon.components.HealthComponent;
import ulb.models.bugemon.components.InitiativeComponent;
import ulb.models.bugemon.components.LevelComponent;
import ulb.models.bugemon.components.modifier.Modifier;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.level_up.Upgrade;

/**
 * Central game entity. Stats are backed by components (see {@code ulb.models.bugemon.components}). Create instances via
 * {@link BugemonBuilder}; equality is based on {@link #id}.
 */
public class Bugemon implements BugemonDTO {
    @SerializedName("id")
    String id;

    /** Display name of this bugemon. Serialised as {@code "nom"}. */
    @SerializedName("nom")
    String name;

    BugemonType type;

    String sprite;

    HealthComponent healthComponent;

    AttackComponent attackComponent;

    DefenseComponent defenseComponent;

    InitiativeComponent initiativeComponent;

    LevelComponent levelComponent;

    @SerializedName("starter")
    boolean isStarter;

    @SerializedName("attaques")
    List<Attack> attackList;

    /** Use {@link BugemonBuilder} to construct instances. */
    Bugemon() {
    }

    /**
     * Copy constructor.
     *
     * @param copy
     *            the {@link Bugemon} to copy
     */
    public Bugemon(Bugemon copy) {
        this.id = copy.getId();
        this.name = copy.getName();
        this.type = copy.getType();
        this.sprite = copy.getSpriteURL();
        this.healthComponent = new HealthComponent(copy.getHp(), copy.getMaxHp());
        this.attackComponent = new AttackComponent(copy.getAttack());
        this.defenseComponent = new DefenseComponent(copy.getDefense());
        this.initiativeComponent = new InitiativeComponent(copy.getInitiative());
        this.levelComponent = new LevelComponent(copy.getXp(), copy.getLevel());
        // Safe because Attack is immutable (record)
        this.attackList = List.copyOf(copy.getAttackList());
        this.isStarter = copy.isStarter();
    }

    /**
     * Decrease the health of this bugemon.
     *
     * @param damage
     *            the amount of damage
     */
    public void takeDamage(int damage) {
        this.healthComponent.decreaseHp(damage);
    }

    @Override
    public boolean isAlive() {
        return this.healthComponent.getHp() > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bugemon other = (Bugemon) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    // Getters and Setters

    /**
     * Get the name of the bugemon.
     *
     * @return (String) the name of the bugemon.
     */
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the type of this bugemon.
     *
     * @return the type
     */
    public BugemonType getType() {
        return this.type;
    }

    /**
     * Get the URL to the sprite of this bugemon.
     *
     * @return the URL
     */
    public String getSpriteURL() {
        return this.sprite;
    }

    /**
     * Get the list of attacks this bugemon can perform.
     *
     * @return the list of attacks
     */
    public List<Attack> getAttackList() {
        return Collections.unmodifiableList(this.attackList);
    }

    /**
     * Get the current HP of this bugemon.
     *
     * @return the current HP
     */
    public int getHp() {
        return this.healthComponent.getHp();
    }

    /**
     * Get the maximum HP of this bugemon.
     *
     * @return the maximum HP
     */
    public int getMaxHp() {
        return this.healthComponent.getMaxHp();
    }

    /**
     * Get the attack of this bugemon.
     *
     * @return the attack
     */
    public int getAttack() {
        return this.attackComponent.getAttack();
    }

    /**
     * Get the defense of this bugemon.
     *
     * @return the defense
     */
    public int getDefense() {
        return this.defenseComponent.getDefense();
    }

    /**
     * Get the initiative of this bugemon.
     *
     * @return the initiative
     */
    public int getInitiative() {
        return this.initiativeComponent.getInitiative();
    }

    /**
     * Returns if this bugemon is starter or not
     *
     * @return true if this bugemon is starter and false otherwise
     */
    public boolean isStarter() {
        return this.isStarter;
    }

    /** Clears all active modifiers on every stat component. */
    public void resetMalus() {
        this.healthComponent.resetMalus();
        this.attackComponent.resetMalus();
        this.defenseComponent.resetMalus();
        this.initiativeComponent.resetMalus();
    }

    @Override
    public int getLevel() {
        return this.levelComponent.getLevel();
    }

    /**
     * Restores the HP of this bugemon to its maximum.
     */
    public void restoreHp() {
        this.healthComponent.restoreHp();
    }

    /**
     * Get the current XP of this bugemon.
     *
     * @return the current XP
     */
    public int getXp() {
        return this.levelComponent.getXp();
    }

    @Override
    public double getXpProgress() {
        return this.levelComponent.getXpProgress();
    }

    /**
     * Adds XP and returns the number of levels crossed.
     *
     * @param xp
     *            the amount of XP to add
     *
     * @return number of level-ups that just occurred
     */
    public int gainXp(int xp) {
        return this.levelComponent.addXp(xp);
    }

    /**
     * Applies the upgrade to the bugemon.
     *
     * @param upgrade
     *            the upgrade
     */
    public void applyUpgrade(Upgrade upgrade) {
        this.healthComponent.increaseMaxHp(upgrade.hp());
        this.attackComponent.increaseAttack(upgrade.attack());
        this.defenseComponent.increaseDefense(upgrade.defense());
        this.initiativeComponent.increaseInitiative(upgrade.initiative());
    }

    /**
     * Applies the effect to the bugemon.
     *
     * @param effect
     *            the effect
     */
    public void apply(Effect effect) {
        effect.applyTo(this);
    }

    /**
     * Applies the stat modifier effect to the bugemon.
     *
     * @param e
     *            stat modifier effect
     */
    public void apply(EffectStatModifier e) {
        Modifier m = (e.duration() == EffectDuration.ONE_TURN) ? new Modifier(e.modifier(), 1)
                : new Modifier(e.modifier());
        switch (e.stat()) {
            case HP -> this.healthComponent.addModifier(m);
            case ATTACK -> this.attackComponent.addModifier(m);
            case DEFENSE -> this.defenseComponent.addModifier(m);
            case INITIATIVE -> this.initiativeComponent.addModifier(m);
            default -> throw new IllegalArgumentException("unknown stat: " + e.stat());
        }
    }

    /**
     * Applies the heal effect to the bugemon.
     *
     * @param e
     *            heal effect
     */
    public void apply(EffectHeal e) {
        this.healthComponent.increaseHp(e.amount());
    }

    /**
     * Applies the resetMalus effect to the bugemon.
     *
     * @param e
     *            resetMalus effect
     */
    public void apply(EffectResetMalus e) {
        this.resetMalus();
    }

    /**
     * Kills the bugemon.
     */
    public void kill() {
        this.takeDamage(this.getHp());
    }

    /**
     * Get the list of attacks ids
     *
     * @return the list of attacks ids
     */
    public List<String> getListAttacksId() {
        return this.attackList.stream().map(Attack::id).toList();
    }

}
