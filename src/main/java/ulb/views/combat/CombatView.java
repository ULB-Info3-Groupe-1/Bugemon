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

    @FXML
    protected BugemonInfoView bugemonTrainerInfo;

    @FXML
    protected BugemonInfoView bugemonOpponentInfo;

    @FXML
    protected ImageView bugemonTrainerImage;

    @FXML
    protected ImageView bugemonOpponentImage;

    @FXML
    protected ActionMenuView actionMenuView;

    @FXML
    protected DialogZoneView dialogZoneView;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @throws IOException
     *             if the {@code Combat.fxml} resource cannot be found or parsed.
     */
    protected CombatView() throws IOException {
        super("/fxml/Combat.fxml");
        this.attackAnimationView = new CombatAnimationView(this.bugemonTrainerImage, this.bugemonOpponentImage);
    }

    // ── Abstract contract ─────────────────────────────────────────────────────

    /**
     * Shows/hides regions specific to the combat mode. Called once by the subclass constructor after FXML injection.
     */
    protected abstract void initCombatMode();

    // ── Dialog zone ───────────────────────────────────────────────────────────

    private void showDialog(String dialog, String additionalInfo) {
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setAdditionalInfo(additionalInfo);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    /**
     * Builds and displays the turn-summary dialog. Only attacks that happened are shown; second is empty when one
     * trainer did not attack.
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

    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    // ── Bugemon display ───────────────────────────────────────────────────────

    protected void updateTrainerBugemon(BugemonDTO trainerBugemon) {
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(trainerBugemon.getSpriteURL(), 256, 256, true, false));
        this.makeTrainerBugemonReappear();
    }

    protected void updateOpponentBugemon(BugemonDTO opponentBugemon) {
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(opponentBugemon.getSpriteURL(), 256, 256, true, false));
        this.makeOpponentBugemonReappear();
    }

    // ── Attack animations ─────────────────────────────────────────────────────

    public void playTrainerAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playTrainerAttackAnimation(onFinished);
    }

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
