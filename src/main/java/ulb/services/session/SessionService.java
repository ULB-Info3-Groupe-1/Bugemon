package ulb.services.session;

import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * Remembers the currently signed-in player across application runs (a "stay logged in" feature).
 *
 * <p>
 * The player name is stored per-OS-user via {@link java.util.prefs.Preferences} — no database row and no password is
 * kept, just enough to skip the login screen on the next launch. Clearing it (logging out) brings the login screen
 * back.
 */
public class SessionService {

    private static final String KEY_PLAYER = "loggedInPlayer";

    private final Preferences preferences;

    public SessionService() {
        this.preferences = Preferences.userNodeForPackage(SessionService.class);
    }

    /**
     * Returns the remembered signed-in player, if any.
     *
     * @return the stored player name, or empty if nobody is remembered
     */
    public Optional<String> getLoggedInPlayer() {
        String name = this.preferences.get(KEY_PLAYER, null);
        return name == null || name.isBlank() ? Optional.empty() : Optional.of(name);
    }

    /**
     * Remembers {@code playerName} as signed in so the next launch skips the login screen.
     *
     * @param playerName
     *            the player to remember
     */
    public void setLoggedInPlayer(String playerName) {
        this.preferences.put(KEY_PLAYER, playerName);
    }

    /** Forgets the signed-in player (logout), so the login screen is shown again next time. */
    public void clearLoggedInPlayer() {
        this.preferences.remove(KEY_PLAYER);
    }
}
