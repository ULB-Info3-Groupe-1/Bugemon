package ulb.models.utils;

/**
 * Simple 2D vector class for representing positions and directions in the game
 * world. Contains a static method for linear interpolation between two vectors,
 * which is used for smooth movement and animations.
 */
public class Vec2 {

    // Attributes

    public float x, y;

    // Constructors

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Methods

    /**
     * Linearly interpolates between two vectors a and b by a factor of t (0 <= t <=
     * 1). When t=0, returns a; when t=1, returns b; when t=0.5, returns the
     * midpoint between a and b.
     * 
     * @param a (Vec2) the starting vector
     * @param b (Vec2) the ending vector
     * @param t (float) the interpolation factor, typically between 0 and 1
     * @return (Vec2) the interpolated vector
     */
    public static Vec2 linearInterpolation(Vec2 a, Vec2 b, float t) {
        return new Vec2(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t);
    }
}
