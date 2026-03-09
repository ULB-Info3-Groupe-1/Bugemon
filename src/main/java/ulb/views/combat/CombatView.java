package ulb.views.combat;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import ulb.common.BugemonDTO;

import ulb.views.View;
import ulb.views.ActionMenuView;
import ulb.views.BugemonTeamView;
import ulb.views.DialogZoneView;

/**
 * CombatView
 *
 * View for the combat screen.
 * The BugemonInfoView components are embedded directly in the FXML
 * and injected via FXML.
 */
public abstract class CombatView extends View {

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
    protected StackPane bugemonTeamPane;
    @FXML
    protected BugemonTeamView bugemonTeamView;
    @FXML
    protected DialogZoneView dialogZoneView;
 

    /**
     * Constructor for CombatView.
     * @throws IOException 
     */
    public CombatView() throws IOException {
        super("/fxml/Combat.fxml");
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
