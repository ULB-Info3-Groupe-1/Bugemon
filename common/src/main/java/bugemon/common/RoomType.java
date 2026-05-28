package bugemon.common;

/**
 * Category of a tower room, which drives both gameplay behaviour and visual styling.
 *
 * <p>
 * Each constant maps to a CSS class (see {@link #cssClass()}) applied to the room's node in the floor-map view.
 */
public enum RoomType {
    /** The entry room where the player begins the floor. */
    START,
    /** A standard combat encounter room. */
    COMBAT,
    /** A boss encounter room placed at the top of the floor. */
    BOSS,
    /** A reward room that grants items or Bugemons after completion. */
    REWARD,
    /** An empty room with no encounter or reward. */
    EMPTY;

    /**
     * Returns the CSS class name for this room type, prefixed with {@code "room-"}.
     *
     * @return a CSS identifier such as {@code "room-combat"} or {@code "room-boss"}
     */
    public String cssClass() {
        return "room-" + this.name().toLowerCase();
    }
}
