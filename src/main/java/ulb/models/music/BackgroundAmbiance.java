package ulb.models.music;

/**
 * Identifies the background music context currently active in the game.
 *
 * <ul>
 * <li>{@link #COMBAT} — music played during a battle.</li>
 * <li>{@link #MENU} — music played in menus and exploration screens.</li>
 * </ul>
 */
public enum BackgroundAmbiance {
    COMBAT,
    MENU
}
