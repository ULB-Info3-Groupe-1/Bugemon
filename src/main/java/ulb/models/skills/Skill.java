package ulb.models.skills;

/**
 * Represents a skill that can be unlocked in the skill tree.
 */
public class Skill {

    private final String id;

    private final String name;

    private final String description;

    private final int cost;

    private int currentLevel;

    private final int maxLevel;

    private final SkillEffect effect;

    private boolean isUnlocked;

    public Skill(String id, String name, String description, int cost, int maxLevel, SkillEffect effect,
            boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.maxLevel = maxLevel;
        this.effect = effect;
        this.isUnlocked = isUnlocked;
        this.currentLevel = isUnlocked ? 1 : 0;
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

    public int getCurrentLevel() {
        return this.currentLevel;
    }

    public void incrementLevel() {
        if (this.currentLevel < this.maxLevel) {
            this.currentLevel++;
        }
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public SkillEffect getEffect() {
        return this.effect;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }

    public void unlock() {
        this.incrementLevel();
        this.isUnlocked = true;
    }
}
