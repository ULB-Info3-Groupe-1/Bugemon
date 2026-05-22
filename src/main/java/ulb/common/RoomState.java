package ulb.common;

/**
 * Navigation status of a floor room as seen by the player.
 *
 * <p>
 * The state determines which CSS style class the room node receives in the floor-map view (see {@link #cssClass()}) and
 * which rooms the player is allowed to enter next.
 */
public enum RoomState {
    /** The room the player is currently occupying. */
    CURRENT,
    /** The room is adjacent to a visited room and can be entered next. */
    AVAILABLE,
    /** The room has already been entered by the player. */
    VISITED,
    /** The room has been visited and is also adjacent to the current position (revisitable). */
    VISITED_AVAILABLE,
    /** The room cannot be reached yet. */
    LOCKED;

    /**
     * Returns the lowercase CSS class name derived from this constant's name, used to style the room node in the
     * floor-map view.
     *
     * @return a lowercase CSS identifier, e.g. {@code "available"} or {@code "visited_available"}
     */
    public String cssClass() {
        return this.name().toLowerCase();
    }
}
