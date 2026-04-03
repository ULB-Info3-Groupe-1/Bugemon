package ulb.models.utils;

/** Immutable 2D vector used for map positions and smooth movement interpolation. */
public class Vec2 {

    private float x;
    private float y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Linearly interpolates between {@code a} and {@code b}.
     *
     * @param t interpolation factor; must be in [0, 1]
     */
    public static Vec2 linearInterpolation(Vec2 a, Vec2 b, float t) {
        return new Vec2(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}
