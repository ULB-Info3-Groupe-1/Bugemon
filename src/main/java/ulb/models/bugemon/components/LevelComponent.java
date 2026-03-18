package ulb.models.bugemon.components;

import ulb.services.LevelUpService;

public class LevelComponent {
    /** Experience points (XP) of the bugemon. */
    int xp;

    /** Level of the bugemon. */
    int level;

    public LevelComponent() {
        this(0, 0);
    }

    public LevelComponent(int xp, int level) {
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
     * Adds xp, and checks whether this implies a level.
     *
     * @param xp the amount of experience points to add
     * @return true if the bugemon has reached a new level
     */
    public boolean addXp(int xp) {
        this.xp += xp;

        if (this.xp == LevelUpService.xpRequiredForLevel(level + 1)) {
            this.xp = 0;
            this.level++;
            return true;
        }

        return false;
    }
}
