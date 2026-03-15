package ulb.fx_controllers.combat;

import java.util.concurrent.CountDownLatch;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import ulb.common.dto.BugemonDTO;
import ulb.controllers.combat.CombatController;
import ulb.fx_controllers.FXController;
import ulb.fx_controllers.combat.components.BugemonInfoComponent;
import ulb.fx_controllers.components.ActionMenuComponent;
import ulb.fx_controllers.components.BugemonTeamComponent;
import ulb.fx_controllers.components.DialogZoneComponent;

/**
 * CombatView
 *
 * View for the combat screen.
 * The BugemonInfoView components are embedded directly in the FXML
 * and injected via FXML.
 */
public abstract class CombatFXController extends FXController{
    protected final CombatController controller;

    @FXML protected DialogZoneComponent dialogZoneView;

    @FXML protected ImageView bugemonTrainerImage;
    @FXML protected BugemonInfoComponent bugemonTrainerInfo;

    @FXML protected BugemonInfoComponent bugemonOpponentInfo;
    @FXML protected ImageView bugemonOpponentImage;

    @FXML protected StackPane bugemonTeamPane;
    @FXML protected BugemonTeamComponent bugemonTeamView;

    @FXML protected ActionMenuComponent actionMenuComponent;

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

        this.dialogZoneView.setOnContinueCallback(this::onContinue);

        // Create a CountDownLatch to block the thread
        CountDownLatch continueLatch = new CountDownLatch(1);
        this.dialogZoneView.setContinueLatch(continueLatch);

        try {
            // Wait for the player to click the "Continue" button
            continueLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while waiting for dialog continuation: " + e.getMessage());
        }
    }

    /**
     * Hide the dialog zone.
     */
    public void hideDialog() {
        this.dialogZoneView.setVisible(false);
        this.dialogZoneView.setManaged(false);
    }

    protected void hideBugemonTeamPane() {
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
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
