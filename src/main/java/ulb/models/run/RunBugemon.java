package ulb.models.run;

import ulb.models.bugemon.PlayerBugemon;

public class RunBugemon {
    private final PlayerBugemon playerBugemon;
    private int currentHp;

    public RunBugemon(PlayerBugemon playerBugemon, int currentHp) {
        if (currentHp < 0) {
            throw new IllegalArgumentException("Bugemon's current hp must be non-negative");
        }

        this.playerBugemon = playerBugemon;
        this.currentHp = currentHp;
    }
}
