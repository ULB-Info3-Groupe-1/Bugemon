package ulb.models.bugemon.components;

/**
 * The level component of a Bugemon
 */
public class LevelComponent {
    private int xp;
    private int level;

    /**
     * Create a new LevelComponent
     *
     * @param xp
     *            the initial XP
     * @param level
     *            the initial level
     */
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

    /**
     * Get the current XP of this bugemon.
     *
     * @return
     */
    public int getXp() {
        return this.xp;
    }

    /**
     * Get the current level of this bugemon.
     *
     * @return the current level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Adds XP and returns the number of levels crossed.
     *
     * @return number of level-ups that just occurred
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
     * Returns XP progress toward the next level as a value in {@code [0.0, 1.0]}.
     *
     * @return XP progress
     */
    public double getXpProgress() {
        return (double) this.xp / this.getXpRequiredForNextLevel(this.level);
    }

    private int getXpRequiredForNextLevel(int targetLevel) {
        return 50 + 100 * (targetLevel - 1);
    }
}
