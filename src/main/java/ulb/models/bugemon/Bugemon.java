package ulb.models.bugemon;

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
public abstract class Bugemon implements BugemonDTO {
    String id;

    @SerializedName("nom")
    String name;

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
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getSpriteURL() {
        return this.sprite;
    }

    public List<Attack> getAttackList() {
        return this.attackList;
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
     * @param choice
     *            the {@link Upgrade} to apply
     */
    // TODO: this should be removed, a choice should know how to apply itself on a
    // bugemon instead.
    public void applyChoice(Upgrade choice) {
        this.healthComponent.increaseMaxHp(choice.hp());
        this.attackComponent.increaseAttack(choice.attack());
        this.defenseComponent.increaseDefense(choice.defense());
        this.initiativeComponent.increaseInitiative(choice.initiative());
    }

    public void addEffect(Effect effect) {
        switch (effect) {
            case EffectStatModifier e -> {
                // TODO: I feel like this part should probably be done elsewhere
                EffectDuration duration = e.duration();
                Modifier modifier = (duration == EffectDuration.ONE_TURN) ? new Modifier(e.modifier(), 1)
                        : new Modifier(e.modifier());

                switch (e.stat()) {
                    case HP -> this.healthComponent.addModifier(modifier);
                    case ATTACK -> this.attackComponent.addModifier(modifier);
                    case DEFENSE -> this.defenseComponent.addModifier(modifier);
                    case INITIATIVE -> this.initiativeComponent.addModifier(modifier);
                    default -> throw new IllegalArgumentException("unknown stat: " + e.stat());
                }
            }

            case EffectHeal e -> this.healthComponent.increaseHp(e.amount());
            case EffectResetMalus e -> this.resetModifiers();

            default -> throw new IllegalArgumentException("unknown effect");
        }
    }

    public void kill() {
        this.takeDamage(this.getHp());
    }

    public List<String> getListAttacksId() {
        return this.attackList.stream().map(Attack::id).toList();
    }
}
