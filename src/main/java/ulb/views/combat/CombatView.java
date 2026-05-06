package ulb.views.combat;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import ulb.Configuration;
import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Efficiency;
import ulb.models.bugemon.Item;
import ulb.models.combat.TurnStep;
import ulb.models.trainer.Trainer;
import ulb.views.View;
import ulb.views.combat.components.BugemonInfoView;
import ulb.views.components.DialogZoneView;
import ulb.views.components.HoverInfoView;

/**
 * Abstract base view for all combat screens, loaded from the shared {@code Combat.fxml} layout. Subclasses implement
 * {@link #initCombatMode()} to configure their specific UI behaviour.
 */
public abstract class CombatView extends View {

    private CombatAnimationView attackAnimationView;

    @FXML
    private BugemonInfoView bugemonTrainerInfo;
    @FXML
    private BugemonInfoView bugemonOpponentInfo;
    @FXML
    private ImageView bugemonTrainerImage;
    @FXML
    private ImageView bugemonOpponentImage;
    @FXML
    private VBox actionMenuSlot;
    @FXML
    private HoverInfoView hoverInfoView;
    @FXML
    private DialogZoneView dialogZoneView;

    NextListener nextListener;

    /** Default constructor. */
    protected CombatView() {
    }

    /** Called by the FXMLLoader after all {@code @FXML} fields are injected. */
    @FXML
    protected void initialize() {
        this.attackAnimationView = new CombatAnimationView(this.bugemonTrainerImage, this.bugemonOpponentImage);
        this.dialogZoneView.setListener(() -> this.nextListener.onNext());
        this.initCombatMode();
    }

    /**
     * Sets the listener to be notified when the next button is clicked.
     *
     * @param listener
     *            the next listener
     */
    public void setNextListener(NextListener listener) {
        this.nextListener = listener;
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.COMBAT_VIEW;
    }

    // ── Abstract contract ─────────────────────────────────────────────────────

    /**
     * Configures UI regions specific to this combat mode. Called once after FXML injection via {@link #initialize()}.
     */
    protected abstract void initCombatMode();

    // ── Action menu ───────────────────────────────────────────────────────────

    /** Replaces the content of the action menu slot with the given node. */
    protected void setActionMenuContent(Node content) {
        this.actionMenuSlot.getChildren().setAll(content);
    }

    // ── Hover info panel ──────────────────────────────────────────────────────

    /**
     * Populates and shows the hover info panel with the given title and lines.
     *
     * @param title
     *            displayed in bold at the top
     * @param lines
     *            zero or more info lines shown below the title
     */
    public void showHoverInfo(String title, String... lines) {
        this.hoverInfoView.show(title, lines);
    }

    /**
     * Applies a type-based background colour to the hover info panel.
     *
     * @param type
     *            the bugemon type whose colour to apply
     */
    public void setHoverType(BugemonType type) {
        this.hoverInfoView.setType(type);
    }

    /**
     * Shows or hides the efficiency badge on the hover info panel.
     *
     * @param eff
     *            the efficiency value to display
     */
    public void setHoverEfficiency(Efficiency eff) {
        this.hoverInfoView.setEfficiency(eff);
    }

    /** Hides the hover info panel. */
    public void hideHoverInfo() {
        this.hoverInfoView.hide();
    }

    // ── Action menu ───────────────────────────────────────────────────────────

    /** Hides the action menu slot from the layout. */
    protected void hideActionMenu() {
        this.actionMenuSlot.setVisible(false);
        this.actionMenuSlot.setManaged(false);
    }

    /** Restores the action menu slot in the layout. */
    protected void showActionMenu() {
        this.actionMenuSlot.setVisible(true);
        this.actionMenuSlot.setManaged(true);
    }

    // ── Dialog zone ───────────────────────────────────────────────────────────

    /** Disables the Next button immediately so rapid clicks cannot queue steps during an animation. */
    public void lockNextButton() {
        this.dialogZoneView.setNextButtonDisabled(true);
    }

    /**
     * Updates only the menus and action slots to reflect the current model state, without touching sprites or HP bars.
     * Override in concrete views that have interactive menus.
     */
    public void refreshMenuState() {
    }

    private void showDialog(String dialog) {
        this.dialogZoneView.setNextButtonDisabled(false);
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    /**
     * Builds and displays a dialog describing the given {@code step}.
     *
     * @param step
     *            the step to display
     * @param playerTrainer
     *            the player trainer that performed the step
     */
    public void showStepDialog(TurnStep step, Trainer playerTrainer) {
        String message = switch (step) {
            case TurnStep.AttackStep(Trainer attacker, Attack attack, Efficiency efficiency) ->
                attacker.getCurrentBugemonName() + " utilise " + attack.name() + " !"
                        + this.formatEfficiency(efficiency);

            case TurnStep.SwitchStep(Trainer trainer, Bugemon bugemon) ->
                (trainer == playerTrainer ? "Vous envoyez " : "L'adversaire envoie ") + bugemon.getName() + " !";

            case TurnStep.ItemStep(Trainer trainer, Item item) ->
                (trainer == playerTrainer ? "Vous utilisez " : "L'adversaire utilise ") + item.name() + " !";

            case TurnStep.BugemonKoStep(Trainer trainer) ->
                trainer == playerTrainer ? "Votre Bugémon est K.O. !" : "Le Bugémon adverse est K.O. !";

            case TurnStep.TrainerKoStep(Trainer trainerKo) ->
                trainerKo == playerTrainer ? "Vous êtes vaincu !" : "L'adversaire est vaincu !";

            case TurnStep.ForfeitStep(Trainer trainer) ->
                trainer == playerTrainer ? "Vous abandonnez..." : "L'adversaire abandonne.";

            default -> "";
        };

        this.showDialog(message);
    }

    private String formatEfficiency(Efficiency efficiency) {
        return switch (efficiency) {
            case HIGH -> " C'est super efficace !";
            case LOW -> " Ce n'est pas très efficace.";
            default -> "";
        };
    }

    /**
     * Hides the dialog zone from the layout.
     */
    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    // ── Bugemon display ───────────────────────────────────────────────────────

    /**
     * Updates the trainer's Bugemon sprite and info bar.
     *
     * @param trainerBugemon
     *            the trainer's Bugemon
     */
    public void updateTrainerBugemon(BugemonDTO trainerBugemon) {
        if (trainerBugemon.isAlive()) {
            this.makeTrainerBugemonReappear();
        }
        File file = new File(Configuration.Paths.SPRITES + trainerBugemon.getSpriteURL());
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
    }

    /**
     * Updates the opponent's Bugemon sprite and info bar.
     *
     * @param opponentBugemon
     *            the opponent's Bugemon
     */
    public void updateOpponentBugemon(BugemonDTO opponentBugemon) {
        File file = new File(Configuration.Paths.SPRITES + opponentBugemon.getSpriteURL());
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
        this.makeOpponentBugemonReappear();
    }

    /**
     * Updates only the info bar (HP, level, XP) without changing the sprite or triggering any animation.
     *
     * @param bugemon
     *            the bugemon to update
     */
    public void updateTrainerInfo(BugemonDTO bugemon) {
        this.bugemonTrainerInfo.setBugemonInfo(bugemon);
    }

    /**
     * Updates only the info bar (HP, level, XP) without changing the sprite or triggering any animation.
     *
     * @param bugemon
     *            the bugemon to update
     */
    public void updateOpponentInfo(BugemonDTO bugemon) {
        this.bugemonOpponentInfo.setBugemonInfo(bugemon);
    }

    // ── Attack animations ─────────────────────────────────────────────────────

    /**
     * Plays the trainer attack animation (lunge forward then back).
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playTrainerAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playTrainerAttackAnimation(onFinished);
    }

    /**
     * Plays the opponent attack animation (lunge forward then back).
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playOpponentAttackAnimation(Runnable onFinished) {
        this.attackAnimationView.playOpponentAttackAnimation(onFinished);
    }

    /**
     * Plays the death animation for the trainer's active Bugemon.
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playDeathAnimationForTrainer(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForTrainer(onFinished);
    }

    /**
     * Plays the death animation for the opponent's active Bugemon.
     *
     * @param onFinished
     *            callback executed after the animation completes
     */
    public void playDeathAnimationForOpponent(Runnable onFinished) {
        this.attackAnimationView.playDeathAnimationForOpponent(onFinished);
    }

    /**
     * Reveals the trainer's Bugemon sprite.
     */
    public void makeTrainerBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonTrainerImage);
    }

    /**
     * Reveals the opponent's Bugemon sprite.
     */
    public void makeOpponentBugemonReappear() {
        this.attackAnimationView.makeBugemonReappear(this.bugemonOpponentImage);
    }

    public interface NextListener {

        /**
         * Notifies the listener that the next button has been clicked.
         */
        void onNext();

    }
}
