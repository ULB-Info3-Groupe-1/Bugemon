package ulb.views.combat;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import ulb.common.dto.BugemonDTO;
import ulb.views.BugemonTeamView;
import ulb.views.DialogZoneView;
import ulb.views.View;

/**
 * Abstract base view for all combat screens.
 *
 * <p>
 * {@code CombatView} loads the shared {@code Combat.fxml} layout and exposes
 * the FXML-injected components that are common to every combat mode:
 * Bugemon info panels, sprite images, the action menu container, the team
 * switcher pane and the dialog zone.
 * </p>
 *
 * <p>
 * Concrete subclasses ({@link AutomaticCombatView}, {@link ManualCombatView})
 * must implement {@link #initCombatMode()} to configure which UI regions are
 * visible and how they behave for their specific mode.
 * </p>
 *
 * <p>
 * The controller layer interacts with the combat UI exclusively through the
 * public methods of this class, keeping all JavaFX node manipulation out of
 * the controller.
 * </p>
 *
 * @see AutomaticCombatView
 * @see ManualCombatView
 */
public abstract class CombatView extends View {
    // ── FXML-injected components ──────────────────────────────────────────────

    /**
     * Info panel (name, type, HP bar) for the player's active Bugemon,
     * displayed on the player's side of the combat screen.
     */
    @FXML protected BugemonInfoView bugemonTrainerInfo;

    /**
     * Info panel (name, type, HP bar) for the opponent's active Bugemon,
     * displayed on the opponent's side of the combat screen.
     */
    @FXML protected BugemonInfoView bugemonOpponentInfo;

    /** Sprite image of the player's currently active Bugemon. */
    @FXML protected ImageView bugemonTrainerImage;

    /** Sprite image of the opponent's currently active Bugemon. */
    @FXML protected ImageView bugemonOpponentImage;

    /**
     * Container for the action menu components (main menu, attack menu, …).
     * Subclasses populate this container via their own menu components.
     */
    @FXML protected ActionMenuView actionMenuView;

    /**
     * Overlay pane that hosts the {@link BugemonTeamView} used to pick a
     * Bugemon during a switch. Hidden by default; shown when a switch is
     * requested.
     */
    @FXML protected StackPane bugemonTeamPane;

    /**
     * Scrollable grid of the player's team members, embedded inside
     * {@link #bugemonTeamPane}. Each cell is clickable when a switch is in
     * progress.
     */
    @FXML protected BugemonTeamView bugemonTeamView;

    /**
     * Overlay banner used to display turn feedback messages such as attack
     * effectiveness or KO notifications. Toggled visible/invisible by
     * {@link #showDialog(String, String)} and {@link #hideDialog()}.
     */
    @FXML protected DialogZoneView dialogZoneView;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Loads the shared {@code Combat.fxml} layout.
     *
     * <p>
     * Subclass constructors must call {@code super()} and then invoke
     * {@link #initCombatMode()} to finalise their mode-specific UI setup.
     * </p>
     *
     * @throws IOException if the {@code Combat.fxml} resource cannot be found
     *                     or parsed.
     */
    public CombatView() throws IOException {
        super("/fxml/Combat.fxml");
    }

    // ── Abstract contract ─────────────────────────────────────────────────────

    /**
     * Configures the combat UI for the specific mode implemented by the
     * subclass.
     *
     * <p>
     * Typical implementations show or hide regions that are irrelevant for
     * their mode (e.g. {@link AutomaticCombatView} hides the action menu and
     * team pane, while {@link ManualCombatView} shows the main action menu).
     * This method is called once by the subclass constructor after the FXML
     * components have been injected.
     * </p>
     */
    protected abstract void initCombatMode();

    // ── Dialog zone ───────────────────────────────────────────────────────────

    /**
     * Displays the dialog zone with the given message and optional additional
     * information.
     *
     * <p>
     * Typical uses include showing attack-effectiveness feedback
     * ("ATTAQUE EFFICACE !") or KO announcements. The dialog zone remains
     * visible until {@link #hideDialog()} is called.
     * </p>
     *
     * @param dialog         the main message to display; must not be
     *                       {@code null}.
     * @param additionalInfo a secondary line of text, or {@code null} if no
     *                       additional information should be shown.
     */
    public void showDialog(String dialog, String additionalInfo) {
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setAdditionalInfo(additionalInfo);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    /**
     * Hides the dialog zone, removing it from the layout flow so that it does
     * not occupy space when empty.
     */
    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    // ── Bugemon display ───────────────────────────────────────────────────────

    /**
     * Updates the player-side info panel and sprite to reflect the given
     * Bugemon's current state (name, type, HP).
     *
     * <p>
     * Should be called by the controller at the start of a combat session and
     * after every turn in which the player's active Bugemon may have changed
     * or taken damage.
     * </p>
     *
     * @param trainerBugemon a {@link BugemonDTO} snapshot of the player's
     *                       currently active Bugemon; must not be {@code null}.
     */
    public void updateTrainerBugemon(BugemonDTO trainerBugemon) {
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(trainerBugemon.getSpriteURL()));
    }

    /**
     * Updates the opponent-side info panel and sprite to reflect the given
     * Bugemon's current state (name, type, HP).
     *
     * <p>
     * Should be called by the controller at the start of a combat session and
     * after every turn in which the opponent's active Bugemon may have changed
     * or taken damage.
     * </p>
     *
     * @param opponentBugemon a {@link BugemonDTO} snapshot of the opponent's
     *                        currently active Bugemon; must not be {@code null}.
     */
    public void updateOpponentBugemon(BugemonDTO opponentBugemon) {
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(opponentBugemon.getSpriteURL()));
    }
}
