package ulb.views;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * View for the main menu screen.
 *
 * <p>
 * Dispatches user interactions to the controller exclusively through callbacks registered via
 * {@link #setOnCreateTeam(Runnable)} and {@link #setOnQuit(Runnable)}. The view holds no reference
 * to any concrete controller class.
 * </p>
 */
public class MainMenuView extends View {
    @FXML
    private Button createTeamButton;
    @FXML
    private Button noTowerButton;
    @FXML
    private Button quitButton;

    private Runnable onCreateTeam;
    private Runnable onNoTower;
    private Runnable onQuit;
    @FXML
    private Button launchAutomaticCombat;
    @FXML
    private Button launchManualCombat;

    /**
     * Loads the main-menu FXML layout and wires the button actions to the registered callbacks.
     *
     * @throws IOException
     *             if the FXML resource cannot be loaded.
     */
    public MainMenuView() throws IOException {
        super("/fxml/MainMenu.fxml");
        this.createTeamButton.setOnAction(e -> {
            if (this.onCreateTeam != null) {
                this.onCreateTeam.run();
            }
        });
        this.noTowerButton.setOnAction(e -> {
            if (this.onNoTower != null) {
                this.onNoTower.run();
            }
        });
        this.quitButton.setOnAction(e -> {
            if (this.onQuit != null) {
                this.onQuit.run();
            }
        });
    }

    /** Registers the callback invoked when the player clicks "Créer une équipe". */
    public void setOnCreateTeam(Runnable callback) {
        this.onCreateTeam = callback;
    }

    /** Registers the callback invoked when the player clicks "Lancer la NO Tower". */
    public void setOnNoTower(Runnable callback) {
        this.onNoTower = callback;
    }

    /** Registers the callback invoked when the player clicks "Quitter". */
    public void setOnQuit(Runnable callback) {
        this.quitButton.setOnAction(e -> callback.run());
    }

    /** Registers the callback invoked when the player launches an automatic combat. */
    public void setOnStartAutoCombat(Runnable callback) {
        this.launchAutomaticCombat.setOnAction(e -> callback.run());
    }

    /** Registers the callback invoked when the player launches a manual combat. */
    public void setOnStartManualCombat(Runnable callback) {
        this.launchManualCombat.setOnAction(e -> callback.run());
    }

    @Override
    public void refresh() {
        // No dynamic content to refresh in the main menu, so this method is empty.
    }
}
