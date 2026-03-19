package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.models.bugemon_team.BugemonTeam;

/**
 * View for the main menu screen.
 *
 * <p>
 * Dispatches user interactions to the controller exclusively through
 * callbacks registered via {@link #setOnCreateTeam(Runnable)} and
 * {@link #setOnQuit(Runnable)}. The view holds no reference to any
 * concrete controller class.
 * </p>
 */
public class MainMenuView extends View {
    @FXML private Button createTeamButton;
    @FXML private Button quitButton;
    @FXML private Button launchAutomaticCombat;
    @FXML private Button launchManualCombat;


    private Runnable onCreateTeam;
    private Runnable onQuit;
    private Runnable onStartAutoCombat;
    private Runnable onStartManualCombat;
    private BugemonTeam bugemonTeam;

    /**
     * Loads the main-menu FXML layout and wires the button actions to the
     * registered callbacks.
     *
     * @throws IOException if the FXML resource cannot be loaded.
     */
    public MainMenuView() throws IOException {
        super("/fxml/MainMenu.fxml");
        this.createTeamButton.setOnAction(e -> {
            if (onCreateTeam != null)
                onCreateTeam.run();
        });
        this.quitButton.setOnAction(e -> {
            if (onQuit != null)
                onQuit.run();
        });

        this.launchAutomaticCombat.setOnAction(e -> launchCombat(onStartAutoCombat));
        this.launchManualCombat.setOnAction(e -> launchCombat(onStartManualCombat));
    }

    /** Registers the callback invoked when the player clicks "Créer une équipe". */
    public void setOnCreateTeam(Runnable callback) {
        this.onCreateTeam = callback;
    }

    /** Registers the callback invoked when the player clicks "Quitter". */
    public void setOnQuit(Runnable callback) {
        this.onQuit = callback;
    }

    @Override
    public void refresh() {
        // No dynamic data to display on the main menu.
    }

    /** Registers the callback invoked when the player launches an automatic combat. */
    public void setOnStartAutoCombat(Runnable callback) {
        this.onStartAutoCombat = callback;
    }

    /** Registers the callback invoked when the player launches a manual combat. */
    public void setOnStartManualCombat(Runnable callback) {
        this.onStartManualCombat = callback;
    }

    private void launchCombat(Runnable onStart) {
        if (bugemonTeam != null && bugemonTeam.isEmpty()) {
            showAlert("Équipe incomplète",
                    "Veuillez sélectionner au moins un Bugemon pour démarrer un combat.");
        } else if (onStart != null) {
            onStart.run();
        }
    }

    /** Gives the view a reference to the team model it should read from. */
    public void setModel(BugemonTeam bugemonTeam) {
        this.bugemonTeam = bugemonTeam;
    }

}
