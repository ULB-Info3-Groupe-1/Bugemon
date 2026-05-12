package ulb.models.combat;

import ulb.models.run.RunBugemon;

public class CombatBugemon {
    private final RunBugemon runBugemon;
    private boolean participated;
    // TODO: effects

    public CombatBugemon(RunBugemon runBugemon) {
        this.runBugemon = runBugemon;
        this.participated = false;
    }

    public boolean hasParticipated() {
        return this.participated;
    }

    public void markAsParticipated() {
        this.participated = true;
    }
}
