package ulb.services.system;

import java.util.prefs.Preferences;

import ulb.Configuration;

/**
 * Persists user-facing application preferences across runs.
 *
 * <p>
 * Settings are stored per-OS-user via {@link java.util.prefs.Preferences} (no database, no file management), so they
 * are available even on the login screen where no player account exists yet. Currently persists the master audio volume
 * and the fullscreen display flag.
 */
public class SettingsService {

    private static final String KEY_VOLUME = "volume";
    private static final String KEY_FULLSCREEN = "fullscreen";

    private final Preferences preferences;

    public SettingsService() {
        this.preferences = Preferences.userNodeForPackage(SettingsService.class);
    }

    /**
     * Returns the persisted master volume, clamped to {@code [0.0, 1.0]}, or {@link Configuration.Ui#DEFAULT_VOLUME} if
     * none was saved.
     *
     * @return the stored master volume
     */
    public double getVolume() {
        double stored = this.preferences.getDouble(KEY_VOLUME, Configuration.Ui.DEFAULT_VOLUME);
        return Math.clamp(stored, 0, 1.0);
    }

    /**
     * Persists the master volume, clamped to {@code [0.0, 1.0]}.
     *
     * @param volume
     *            the volume to store
     */
    public void setVolume(double volume) {
        this.preferences.putDouble(KEY_VOLUME, Math.clamp(volume, 0, 1.0));
    }

    /**
     * Returns the persisted fullscreen preference, defaulting to {@code false} (windowed) if none was saved.
     *
     * @return {@code true} if the window should start fullscreen
     */
    public boolean isFullScreen() {
        return this.preferences.getBoolean(KEY_FULLSCREEN, false);
    }

    /**
     * Persists the fullscreen preference.
     *
     * @param fullscreen
     *            {@code true} to remember fullscreen, {@code false} for windowed
     */
    public void setFullScreen(boolean fullscreen) {
        this.preferences.putBoolean(KEY_FULLSCREEN, fullscreen);
    }
}
