package ulb.views.combat;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import ulb.common.Efficiency;
import ulb.common.dto.BugemonDTO;
import ulb.models.combat.TurnResult;
import ulb.views.DialogZoneView;
import ulb.views.View;

/**
 * Abstract base view for all combat screens.
 *
 * <p>
 * {@code CombatView} loads the shared {@code Combat.fxml} layout and exposes the FXML-injected components that are
 * common to every combat mode: Bugemon info panels, sprite images, the action menu container, the team switcher pane
 * and the dialog zone.
 * </p>
 *
 * <p>
 * Concrete subclasses ({@link AutomaticCombatView}, {@link ManualCombatView}) must implement {@link #initCombatMode()}
 * to configure which UI regions are visible and how they behave for their specific mode.
 * </p>
 *
 * <p>
 * The controller layer interacts with the combat UI exclusively through the public methods of this class, keeping all
 * JavaFX node manipulation out of the controller.
 * </p>
 *
 * @see AutomaticCombatView
 * @see ManualCombatView
 */
public abstract class CombatView extends View {
    private final CombatAnimationView attackAnimationView;

    // ── FXML-injected components ──────────────────────────────────────────────

    /**
     * Info panel (name, type, HP bar) for the player's active Bugemon, displayed on the player's side of the combat
     * screen.
     */
    @FXML
    protected BugemonInfoView bugemonTrainerInfo;

    /**
     * Info panel (name, type, HP bar) for the opponent's active Bugemon, displayed on the opponent's side of the combat
     * screen.
     */
    @FXML
    protected BugemonInfoView bugemonOpponentInfo;

    /** Sprite image of the player's currently active Bugemon. */
    @FXML
    protected ImageView bugemonTrainerImage;

    /** Sprite image of the opponent's currently active Bugemon. */
    @FXML
    protected ImageView bugemonOpponentImage;

    /**
     * Container for the action menu components (main menu, attack menu, …). Subclasses populate this container via
     * their own menu components.
     */
    @FXML
    protected ActionMenuView actionMenuView;

    /**
     * Overlay banner used to display turn feedback messages such as attack effectiveness or KO notifications. Toggled
     * visible/invisible by {@link #showDialog(String, String)} and {@link #hideDialog()}.
     */
    @FXML
    protected DialogZoneView dialogZoneView;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Loads the shared {@code Combat.fxml} layout.
     *
     * <p>
     * Subclass constructors must call {@code super()} and then invoke {@link #initCombatMode()} to finalise their
     * mode-specific UI setup.
     * </p>
     *
     * @throws IOException
     *             if the {@code Combat.fxml} resource cannot be found or parsed.
     */
    protected CombatView() throws IOException {
        super("/fxml/Combat.fxml");
        this.attackAnimationView = new CombatAnimationView(this.bugemonTrainerImage, this.bugemonOpponentImage);
    }

    // ── Abstract contract ─────────────────────────────────────────────────────

    /**
     * Configures the combat UI for the specific mode implemented by the subclass.
     *
     * <p>
     * Typical implementations show or hide regions that are irrelevant for their mode (e.g. {@link AutomaticCombatView}
     * hides the action menu and team pane, while {@link ManualCombatView} shows the main action menu). This method is
     * called once by the subclass constructor after the FXML components have been injected.
     * </p>
     */
    protected abstract void initCombatMode();

    // ── Dialog zone ───────────────────────────────────────────────────────────

    /**
     * Displays the dialog zone with the given message and optional additional information.
     *
     * <p>
     * Typical uses include showing attack-effectiveness feedback ("ATTAQUE EFFICACE !") or KO announcements. The dialog
     * zone remains visible until {@link #hideDialog()} is called.
     * </p>
     *
     * @param dialog
     *            the main message to display; must not be {@code null}.
     * @param additionalInfo
     *            a secondary line of text, or {@code null} if no additional information should be shown.
     */
    private void showDialog(String dialog, String additionalInfo) {
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setAdditionalInfo(additionalInfo);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    /**
     * Builds and displays the turn-summary dialog from the two attack results of the last resolved turn. Only the
     * attacks that actually happened are shown; the second result is absent when one trainer did not attack.
     *
     * @param firstAttackResult
     *            result of the first attack; never {@code null}.
     * @param secondAttackResult
     *            result of the second attack, or empty if only one attack was made this turn.
     */
    public void showCombatDialog(TurnResult.AttackResult firstAttackResult,
            Optional<TurnResult.AttackResult> secondAttackResult) {
        String message = "1- " + firstAttackResult.attacker().getCurrentBugemonName() + " à utilisé l'attaque "
                + firstAttackResult.getAttackName() + "\n";
        String efficiency = "1- " + this.formatEfficiency(firstAttackResult.efficiency()) + "\n";

        if (secondAttackResult.isPresent()) {
            message += "2- " + secondAttackResult.orElseThrow().attacker().getCurrentBugemonName()
                    + " à utilisé l'attaque " + secondAttackResult.orElseThrow().getAttackName();
            efficiency += "2- " + this.formatEfficiency(secondAttackResult.orElseThrow().efficiency());
        }
        this.showDialog(message, efficiency);
    }

    /** Converts an {@link Efficiency} value to a human-readable French label. */
    protected String formatEfficiency(Efficiency efficiency) {
        switch (efficiency) {
            case HIGH :
                return "ATTAQUE EFFICACE: félicitation";
            case LOW :
                return "Peu d'effet ...";
            case NEUTRAL :
            default :
                return "Dégats standards";
        }
    }

    /**
     * Hides the dialog zone, removing it from the layout flow so that it does not occupy space when empty.
     */
    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    // ── Bugemon display ───────────────────────────────────────────────────────

    /**
     * Updates the player-side info panel and sprite to reflect the given Bugemon's current state (name, type, HP).
     *
     * <p>
     * Should be called by the controller at the start of a combat session and after every turn in which the player's
     * active Bugemon may have changed or taken damage.
     * </p>
     *
     * @param trainerBugemon
     *            a {@link BugemonDTO} snapshot of the player's currently active Bugemon; must not be {@code null}.
     */
    protected void updateTrainerBugemon(BugemonDTO trainerBugemon) {
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(trainerBugemon.getSpriteURL(), 256, 256, true, false));
        this.makeTrainerBugemonReappear();
    }

    /**
     * Updates the opponent-side info panel and sprite to reflect the given Bugemon's current state (name, type, HP).
     *
     * @param opponentBugemon
     *            a {@link BugemonDTO} snapshot of the opponent's currently active Bugemon; must not be {@code null}.
     */
    protected void updateOpponentBugemon(BugemonDTO opponentBugemon) {
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(opponentBugemon.getSpriteURL(), 256, 256, true, false));
        this.makeOpponentBugemonReappear();
    }

    // ── Attack animations ─────────────────────────────────────────────────────

    /**
     * Plays a lunge animation on the player's sprite (slide toward the opponent then return), then invokes
     * {@code onFinished} on the JavaFX thread.
     *
     * @param onFinished
     *            callback executed once the animation completes; must not be {@code null}.
     */
    public void playTrainerAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playTrainerAttackAnimation(onFinished);
    }

    /**
     * Plays a lunge animation on the opponent's sprite (slide toward the player then return), then invokes
     * {@code onFinished} on the JavaFX thread.
     *
     * @param onFinished
     *            callback executed once the animation completes; must not be {@code null}.
     */
    public void playOpponentAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playOpponentAttackAnimation(onFinished);
    }

    public void playDeathAnimationForTrainer(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForTrainer(onFinished);
    }

    public void playDeathAnimationForOpponent(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForOpponent(onFinished);
    }

    public void makeTrainerBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonTrainerImage);
    }

    public void makeOpponentBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonOpponentImage);
    }
}
