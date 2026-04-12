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

    public void takeDamage(int damage) {
        this.healthComponent.decreaseHp(damage);
    }

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

    public BugemonType getType() {
        return this.type;
    }

    public String getSpriteURL() {
        return this.sprite;
    }

    public List<Attack> getAttackList() {
        return Collections.unmodifiableList(this.attackList);
    }

    public int getHp() {
        return this.healthComponent.getHp();
    }

    public int getMaxHp() {
        return this.healthComponent.getMaxHp();
    }

    public int getAttack() {
        return this.attackComponent.getAttack();
    }

    public int getDefense() {
        return this.defenseComponent.getDefense();
    }

    public int getInitiative() {
        return this.initiativeComponent.getInitiative();
    }

    public boolean isStarter() {
        return this.isStarter;
    }

    /** Clears all active modifiers on every stat component. */
    public void resetModifiers() {
        this.healthComponent.clearModifiers();
        this.attackComponent.clearModifiers();
        this.defenseComponent.clearModifiers();
        this.initiativeComponent.clearModifiers();
    }

    @Override
    public int getLevel() {
        return this.levelComponent.getLevel();
    }

    public void restoreHp() {
        this.healthComponent.restoreHp();
    }

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
     * @return number of level-ups that just occurred
     */
    public int gainXp(int xp) {
        return this.levelComponent.addXp(xp);
    }

    /**
     * Applies a level-up upgrade's stat bonuses to this Bugemon.
     *
     * @param upgrade
     *            the {@link Upgrade} to apply
     */
    public void applyUpgrade(Upgrade upgrade) {
        this.healthComponent.increaseMaxHp(upgrade.hp());
        this.attackComponent.increaseAttack(upgrade.attack());
        this.defenseComponent.increaseDefense(upgrade.defense());
        this.initiativeComponent.increaseInitiative(upgrade.initiative());
    }

    public void apply(Effect effect) {
        effect.applyTo(this);
    }

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

    public void apply(EffectHeal e) {
        this.healthComponent.increaseHp(e.amount());
    }

    public void apply(EffectResetMalus e) {
        this.resetModifiers();
    }

    public void kill() {
        this.takeDamage(this.getHp());
    }

    public List<String> getListAttacksId() {
        return this.attackList.stream().map(Attack::id).toList();
    }

}
