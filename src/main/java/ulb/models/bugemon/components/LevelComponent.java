package ulb.models.bugemon.components;

public class LevelComponent {
    private int xp;
    private int level;

    public LevelComponent(int xp, int level) {
        if (xp < 0) {
            throw new IllegalArgumentException("xp must be positive");
        }
        if (level < 1) {
            throw new IllegalArgumentException("level must be greater than or equal to 1");
        }

        this.xp = xp;
        this.level = level;
    }

    public int getXp() {
        return this.xp;
    }

    public int getLevel() {
        return this.level;
    }

    /**
     * Adds xp, and returns the number of levels that have just been crossed
     *
     * @param xp
     *            the amount of experience points to add
     * @return the number of levels that have just been crossed
     */
    public int addXp(int xpToAdd) {
        int numLevelUps = 0;

        this.xp += xpToAdd;

        while (this.xp >= this.getXpRequiredForNextLevel(this.level)) {
            this.xp -= this.getXpRequiredForNextLevel(this.level);
            numLevelUps++;
            this.level++;
        }

        return numLevelUps;
    }

    /**
     * Computes XP required to level up
     *
     * @param level
     *            current level
     * @return required XP to level up
     */
    private int getXpRequiredForNextLevel(int targetLevel) {
        return 50 + 100 * (targetLevel - 1);
    }
}
