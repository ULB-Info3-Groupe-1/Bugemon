package ulb.controllers.menu;

import javafx.stage.Stage;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.services.system.MusicService;
import ulb.services.system.SettingsService;
import ulb.views.ViewLoader;
import ulb.views.menu.SettingsView;

/**
 * Controller for the settings dialog.
 *
 * <p>
 * Unlike the navigable screen controllers it does not extend {@link Controller}: the settings panel is shown as a modal
 * overlay rather than swapped into the main scene. It owns the only two pieces of state the dialog can mutate — the
 * {@link MusicService} master volume and the primary {@link Stage} display mode — and persists each change through
 * {@link SettingsService}. Fullscreen persistence is handled by a {@code fullScreenProperty} listener in
 * {@link MetaController} so it also captures exits triggered by the Escape key.
 */
public class SettingsController implements SettingsView.Listener {

    private final MusicService musicService;
    private final SettingsService settingsService;
    private final Stage stage;
    private SettingsView view;

    public SettingsController(MusicService musicService, SettingsService settingsService, Stage stage) {
        this.musicService = musicService;
        this.settingsService = settingsService;
        this.stage = stage;
    }

    /**
     * Opens the settings dialog, seeded with the current audio volume and display mode.
     */
    public void open() {
        if (this.view == null) {
            this.view = ViewLoader.load(SettingsView::new);
            this.view.setListener(this);
        }
        this.view.initState(this.musicService.getVolume(), this.stage.isFullScreen());
        this.view.showModal(this.stage);
    }

    @Override
    public void onVolumeChanged(double volume) {
        this.musicService.setVolume(volume);
        this.settingsService.setVolume(volume);
    }

    @Override
    public void onDisplayModeChanged(SettingsView.DisplayMode mode) {
        boolean fullscreen = mode == SettingsView.DisplayMode.FULLSCREEN;
        // Persist the explicit user choice directly (like volume). Relying on a
        // fullScreenProperty listener is
        // unreliable: JavaFX flips the property back to false while the window closes,
        // clobbering the saved value.
        this.settingsService.setFullScreen(fullscreen);
        this.stage.setFullScreen(fullscreen);
        if (!fullscreen) {
            // Leaving fullscreen returns the window to a normal frame; keep it maximized
            // for consistency.
            this.stage.setMaximized(true);
        }
    }
}
