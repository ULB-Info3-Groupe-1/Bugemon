package ulb.models.bugemon;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

// TODO: check if we could make this become a record

/**
 * Represents a Bugemon.
 */ 
public class Bugemon {
    private final String id;
    private final String name;
    private final int hp;
    private final int attack;
    private final int defense;
    private final int initiative;
    private final BugemonType type;
    private final List<Attack> attacks;
    private final String spritePath;
    private final boolean isStarter;
    private final boolean isBoss;

    public Bugemon(String id, String name, int hp, int attack, int defense, int initiative, BugemonType type,
            List<Attack> attacks, String spritePath, boolean isStarter) {
        this(id, name, hp, attack, defense, initiative, type, attacks, spritePath, isStarter, false);
    }

    public Bugemon(String id, String name, int hp, int attack, int defense, int initiative, BugemonType type,
            List<Attack> attacks, String spritePath, boolean isStarter, boolean isBoss) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.initiative = initiative;
        this.type = Objects.requireNonNull(type);
        this.attacks = List.copyOf(attacks); // TODO: do we really need a copy here?
        this.spritePath = spritePath;
        this.isStarter = isStarter;
        this.isBoss = isBoss;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getHp() {
        return this.hp;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public int getInitiative() {
        return this.initiative;
    }

    public BugemonType getType() {
        return this.type;
    }

    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(this.attacks);
    }

    public String getSpritePath() {
        return this.spritePath;
    }

    public boolean isStarter() {
        return this.isStarter;
    }

    public boolean isBoss() {
        return this.isBoss;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: decide which equals behavior we need: instanceof vs getclass
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
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s [%s] HP:%d ATK:%d DEF:%d INIT:%d", this.name, this.type, this.hp, this.attack,
                this.defense, this.initiative);
    }

    // -----------------------------------------------------
    // CODE BELOW SHOULD MOVE
    // -----------------------------------------------------
    //
    // static List<Attack> validateAndCopyAttackList(List<Attack> attacks) {
    // if (attacks.size() != ATTACKS_COUNT) {
    // throw new InvalidAttackCountException(ATTACKS_COUNT, attacks.size());
    // }
    // return List.copyOf(attacks);
    // }
    //
    // public void takeDamage(int damage) {
    // this.healthComponent.decreaseHp(damage);
    // }
    //
    // @Override
    // public boolean isAlive() {
    // return this.healthComponent.getHp() > 0;
    // }
    //
    //
    // // Getters and Setters
    //
    // /**
    // * Get the name of the bugemon.
    // *
    // * @return (String) the name of the bugemon.
    // */
    //
    // /** Clears all active modifiers on every stat component. */
    // public void resetMalus() {
    // this.healthComponent.resetMalus();
    // this.attackComponent.resetMalus();
    // this.defenseComponent.resetMalus();
    // this.initiativeComponent.resetMalus();
    // }
    //
    // @Override
    // public int getLevel() {
    // return this.levelComponent.getLevel();
    // }
    //
    // public void restoreHp() {
    // this.healthComponent.restoreHp();
    // }
    //
    // public int getXp() {
    // return this.levelComponent.getXp();
    // }
    //
    // @Override
    // public double getXpProgress() {
    // return this.levelComponent.getXpProgress();
    // }
    //
    // /**
    // * Adds XP and returns the number of levels crossed.
    // *
    // * @return number of level-ups that just occurred
    // */
    // public int gainXp(int xp) {
    // return this.levelComponent.addXp(xp);
    // }
    //
    // public void applyUpgrade(Upgrade upgrade) {
    // this.healthComponent.increaseMaxHp(upgrade.hp());
    // this.attackComponent.increaseAttack(upgrade.attack());
    // this.defenseComponent.increaseDefense(upgrade.defense());
    // this.initiativeComponent.increaseInitiative(upgrade.initiative());
    // }
    //
    // public void apply(Effect effect) {
    // effect.applyTo(this);
    // }
    //
    // public void apply(EffectStatModifier e) {
    // Modifier m = (e.duration() == EffectDuration.ONE_TURN) ? new Modifier(e.modifier(), 1)
    // : new Modifier(e.modifier());
    // switch (e.stat()) {
    // case HP -> this.healthComponent.addModifier(m);
    // case ATTACK -> this.attackComponent.addModifier(m);
    // case DEFENSE -> this.defenseComponent.addModifier(m);
    // case INITIATIVE -> this.initiativeComponent.addModifier(m);
    // default -> throw new IllegalArgumentException("unknown stat: " + e.stat());
    // }
    // }
    //
    // public void apply(EffectHeal e) {
    // this.healthComponent.increaseHp(e.amount());
    // }
    //
    // public void apply(EffectResetMalus e) {
    // this.resetMalus();
    // }
    //
    // public void kill() {
    // this.takeDamage(this.getHp());
    // }
    //
    // public List<String> getListAttacksId() {
    // return this.attackList.stream().map(Attack::id).toList();
    // }

}
