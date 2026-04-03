package ulb.models.trainer;

import ulb.models.utils.Vec2;

/** Handles smooth interpolated movement of a trainer between two map positions. */
public class TrainerWalk {

    private Vec2 initPos;
    private Vec2 endPos;
    private Vec2 pos;
    private float walkProgress; // 0 to 1
    private boolean isMoving = false;
    private final float duration = 0.5f;

    public TrainerWalk(Vec2 startPos) {
        this.pos = new Vec2(startPos.getX(), startPos.getY());
    }

    /**
     * Initiates movement towards a new position. Ignored if already moving.
     *
     * @param newPos
     *            the target position to walk to
     */
    public void moveTo(Vec2 newPos) {
        if (!this.isMoving) {
            this.initPos = new Vec2(this.pos.getX(), this.pos.getY());
            this.endPos = new Vec2(newPos.getX(), newPos.getY());
            this.walkProgress = 0f;
            this.isMoving = true;
        }
    }

    /**
     * Advances the walk animation. Must be called every frame.
     *
     * @param deltaTime
     *            elapsed time since last call, in seconds
     */
    public void update(float deltaTime) {
        if (!this.isMoving) {
            return;
        }

        this.walkProgress += deltaTime / this.duration;

        if (this.walkProgress >= 1f) {
            this.walkProgress = 1f;
            this.isMoving = false;
            this.pos = new Vec2(this.endPos.getX(), this.endPos.getY());
            this.onArrival();
        } else {
            this.pos = Vec2.linearInterpolation(this.initPos, this.endPos, this.walkProgress);
        }
    }

    private void onArrival() {
        // TODO : notifier la salle visitée pour la griser
        // ex : map.markVisited(pos);
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    public Vec2 getPosition() {
        return this.pos;
    }
}
