package ulb.models.trainer;

import ulb.models.utils.Vec2;

public class TrainerWalk {
    /**
     * Model for a trainer walking from one position to another on the map. Contains the current position and handles
     * the interpolation logic for smooth movement.
     */

    // Attributes

    private Vec2 initPos;
    private Vec2 endPos;
    private Vec2 pos;
    private float walkProgress; // 0 to 1
    private boolean isMoving = false;
    private final float duration = 0.5f;

    // Constructors

    public TrainerWalk(Vec2 startPos) {
        this.pos = new Vec2(startPos.getX(), startPos.getY());
    }

    // Methods

    /**
     * Initiates movement towards a new position. If already moving, this call is ignored to prevent interrupting the
     * current walk.
     *
     * @param newPos
     *            (Vec2) the target position to walk to.
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
     * Updates the trainer's position based on the elapsed time since the last update. Should be called regularly (e.g.,
     * every frame) to ensure smooth movement. When the walk is complete, the position is set to the target and the
     * movement flag is cleared.
     *
     * @param deltaTime
     *            (float) the time elapsed since the last update, in seconds.
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

    /**
     * Checks if the trainer is currently moving towards a target position.
     *
     * @return (boolean) true if the trainer is in the process of walking, false otherwise.
     */
    public boolean isMoving() {
        return this.isMoving;
    }

    /**
     * Returns the current position of the trainer, which may be in between the initial and target positions if the
     * trainer is currently walking.
     *
     * @return (Vec2) the current position of the trainer on the map.
     */
    public Vec2 getPosition() {
        return this.pos;
    }
}
