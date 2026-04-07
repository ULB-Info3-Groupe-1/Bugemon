package ulb.views.combat;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import ulb.common.Efficiency;
import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.BugemonType;
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
    private static final String FXML_PATH = "/fxml/Combat.fxml";

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

    protected CombatView() {
    }

    /** Called by the FXMLLoader after all {@code @FXML} fields are injected. */
    @FXML
    protected void initialize() {
        this.attackAnimationView = new CombatAnimationView(this.bugemonTrainerImage, this.bugemonOpponentImage);

        this.dialogZoneView.setListener(new DialogZoneView.Listener() {

            @Override
            public void onNext() {
                CombatView.this.nextListener.onNext();
            }

        });

        this.initCombatMode();
    }

    public void setNextListener(NextListener listener) {
        this.nextListener = listener;
    }

    @Override
    public String getPath() {
        return FXML_PATH;
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

    /** Populates and shows the hover info panel with the given title and lines. */
    public void showHoverInfo(String title, String... lines) {
        this.hoverInfoView.show(title, lines);
    }

    /** Applies a type-based background colour to the hover info panel. */
    public void setHoverType(BugemonType type) {
        this.hoverInfoView.setType(type);
    }

    /** Shows or hides the efficiency badge on the hover info panel. */
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

    private void showDialog(String dialog) {
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    /** Builds and displays a dialog describing the given {@code step}. */
    public void showStepDialog(TurnStep step, Trainer playerTrainer) {
        String message = switch (step) {
            case TurnStep.AttackStep s -> s.attacker().getCurrentBugemonName() + " utilise " + s.getAttackName() + " !"
                    + this.formatEfficiency(s.efficiency());
            case TurnStep.SwitchStep s -> (s.trainer() == playerTrainer ? "Vous envoyez " : "L'adversaire envoie ")
                    + s.getBugemon().getName() + " !";
            case TurnStep.ItemStep s ->
                (s.trainer() == playerTrainer ? "Vous utilisez " : "L'adversaire utilise ") + s.getItemName() + " !";
            case TurnStep.BugemonKoStep s ->
                s.trainer() == playerTrainer ? "Votre Bugémon est K.O. !" : "Le Bugémon adverse est K.O. !";
            case TurnStep.TrainerKoStep s ->
                s.trainerKo() == playerTrainer ? "Vous êtes vaincu !" : "L'adversaire est vaincu !";
            case TurnStep.ForfeitStep s ->
                s.trainer() == playerTrainer ? "Vous abandonnez..." : "L'adversaire abandonne.";
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

    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    // ── Bugemon display ───────────────────────────────────────────────────────

    protected void updateTrainerBugemon(BugemonDTO trainerBugemon) {
        File file = new File("assets/sprites/" + trainerBugemon.getSpriteURL());
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
        this.makeTrainerBugemonReappear();
    }

    protected void updateOpponentBugemon(BugemonDTO opponentBugemon) {
        File file = new File("assets/sprites/" + opponentBugemon.getSpriteURL());
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
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

    public interface NextListener {

        void onNext();

    }
}
