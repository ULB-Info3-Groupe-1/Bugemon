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

    private int getXpRequiredForNextLevel(int targetLevel) {
        return 50 + 100 * (targetLevel - 1);
    }
}
