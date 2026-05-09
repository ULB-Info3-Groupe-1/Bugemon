package ulb.models.skills;

import java.util.List;

import ulb.models.bugemon.effect.Effect;

/**
 * Represents a skill that can be unlocked in the skill tree.
 */
public class Skill {

    private String id;

    private String name;

    private String description;

    private int cost;

    private int maxLevel;

    private Effect effect;

    private boolean isUnlocked;

    public Skill(String id, String name, String description, int cost, int maxLevel, Effect effect,
            List<String> prerequisites) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.maxLevel = maxLevel;
        this.effect = effect;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCost() {
        return this.cost;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public Effect getEffect() {
        return this.effect;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }

}
