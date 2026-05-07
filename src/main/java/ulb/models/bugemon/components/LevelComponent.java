package ulb.models.bugemon.components;

public class LevelComponent {
    private int xp;
    private int level;

    /**
     * Constructs a LevelComponent.
     *
     * @param xp
     *            the amount of xp within the current level (must be non-negative)
     * @param level
     *            the current level (must be at least 1)
     * @throws IllegalArgumentException
     *             if xp < 0 or level < 1
     */
    public LevelComponent(int xp, int level) {
        if (xp < 0) {
            throw new IllegalArgumentException("xp must be non-negative");
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
     * Adds XP, updates level if needed.
     *
     * @return number of level-ups that just occurred
     */
    public int addXp(int xpToAdd) {
        if (xpToAdd < 0) {
            throw new IllegalArgumentException("amount of xp to add must be non-negative");
        }

        int numLevelUps = 0;

        this.xp += xpToAdd;

        while (this.xp >= this.getXpRequiredForNextLevel(this.level)) {
            this.xp -= this.getXpRequiredForNextLevel(this.level);
            numLevelUps++;
            this.level++;
        }

        return numLevelUps;
    }

    /** Returns XP progress toward the next level as a value in {@code [0.0, 1.0]}. */
    public double getXpProgress() {
        return (double) this.xp / this.getXpRequiredForNextLevel(this.level);
    }

    /** Returns the XP amount required for reaching the next level */
    private int getXpRequiredForNextLevel(int targetLevel) {
        return 50 + 100 * (targetLevel - 1);
    }
}
