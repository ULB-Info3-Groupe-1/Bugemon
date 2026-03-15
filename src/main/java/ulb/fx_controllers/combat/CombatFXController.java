package ulb.fx_controllers.combat;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import ulb.common.dto.BugemonDTO;
import ulb.controllers.combat.CombatController;
import ulb.fx_controllers.DialogZoneView;
import ulb.fx_controllers.FXController;
import ulb.fx_controllers.components.BugemonTeamComponent;

/**
 * CombatView
 *
 * View for the combat screen.
 * The BugemonInfoView components are embedded directly in the FXML
 * and injected via FXML.
 */
public abstract class CombatFXController extends FXController{
    private final CombatController controller;

    @FXML protected DialogZoneView dialogZoneView;

    @FXML protected ImageView bugemonTrainerImage;
    @FXML protected BugemonInfoView bugemonTrainerInfo;

    @FXML protected BugemonInfoView bugemonOpponentInfo;
    @FXML protected ImageView bugemonOpponentImage;

    @FXML protected StackPane bugemonTeamPane;
    @FXML protected BugemonTeamComponent bugemonTeamView;

    @FXML protected ActionMenuView actionMenuView;

    public CombatFXController(CombatController controller) {
        this.controller = controller;
    }


    /**
     * Initialize the combat mode for this view.
     */
    protected abstract void initCombatMode();

    /**
     * Show a dialog in the dialog zone with the given text and additional info.
     * @param dialog
     * @param additionalInfo
     */
    public void showDialog(String dialog, String additionalInfo) {
        this.dialogZoneView.setDialogText(dialog);
        this.dialogZoneView.setAdditionalInfo(additionalInfo);
        this.dialogZoneView.setVisible(true);
        this.dialogZoneView.setManaged(true);
    }

    /**
     * Hide the dialog zone.
     */
    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    @FXML
    private void onContinue() {
        this.controller.onContinue();
    }

    /**
     * Update the trainer's bugemon info and image in the UI.
     * @param trainerBugemon
     */
    public void updateTrainerBugemon(BugemonDTO trainerBugemon) {
        this.bugemonTrainerInfo.setBugemonInfo(trainerBugemon);
        this.bugemonTrainerImage.setImage(new Image(trainerBugemon.getSpriteURL()));
    }

    /**
     * Update the opponent's bugemon info and image in the UI.
     * @param opponentBugemon
     */
    public void updateOpponentBugemon(BugemonDTO opponentBugemon) {
        this.bugemonOpponentInfo.setBugemonInfo(opponentBugemon);
        this.bugemonOpponentImage.setImage(new Image(opponentBugemon.getSpriteURL()));
    }
}
