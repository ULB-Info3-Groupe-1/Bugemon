package bugemon.common.models.utils;

/**
 * An immutable grid coordinate used throughout the model and view layers.
 *
 * <p>
 * Coordinates are expressed as integer column ({@code x}) and row ({@code y}) indices within a bounded grid. Used
 * primarily by {@link bugemon.common.models.tower.FloorMap} to record room positions and by the floor-map view to place UI
 * elements.
 *
 * @param x
 *            column index (0-based)
 * @param y
 *            row index (0-based)
 */
public record Position(int x, int y) {
}
