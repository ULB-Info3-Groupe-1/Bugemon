package ulb.models.utils;

/**
 * Simple 2D vector class for representing positions and directions in the game world. Contains a static method for
 * linear interpolation between two vectors, which is used for smooth movement and animations.
 *
 * @param x
 *            the x coordinate
 * @param y
 *            the y coordinate
 */
public record Vec2(float x, float y) {

    // Methods

    /**
     * Linearly interpolates between {@code a} and {@code b}.
     *
     * @param t
     *            interpolation factor; must be in [0, 1]
     */
    public static Vec2 linearInterpolation(Vec2 a, Vec2 b, float t) {
        return new Vec2(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
    }
}
