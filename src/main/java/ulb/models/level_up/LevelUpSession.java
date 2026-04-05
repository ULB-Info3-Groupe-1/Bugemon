package ulb.models.level_up;

import java.util.List;

/**
 * Holds the state of an ongoing level-up sequence. The controller populates it via {@link #start(List)} and advances
 * it via {@link #advance()}. The view reads {@link #getCurrent()} in its {@code refresh()} — it never receives data
 * pushed by the controller.
 */
public class LevelUpSession {
    private List<LevelUp> levelUps;
    private int currentIdx;

    /** Starts a new session with the given list of level-up events. */
    public void start(List<LevelUp> newLevelUps) {
        this.levelUps = List.copyOf(newLevelUps);
        this.currentIdx = 0;
    }

    /** Returns the level-up event currently being displayed. */
    public LevelUp getCurrent() {
        return this.levelUps.get(this.currentIdx);
    }

    /** Advances to the next level-up event. */
    public void advance() {
        this.currentIdx++;
    }

    /** Returns {@code true} if there is at least one more event after the current one. */
    public boolean hasNext() {
        return this.currentIdx < this.levelUps.size() - 1;
    }

    /** Returns {@code true} if the session has been started with a non-empty list. */
    public boolean isStarted() {
        return this.levelUps != null && !this.levelUps.isEmpty();
    }
}
