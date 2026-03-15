package ulb.fx_controllers;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.common.dto.BugemonDTO;
import ulb.controllers.CreateTeamController;
import ulb.controllers.MetaController.Window;
import ulb.fx_controllers.components.BugemonTeamComponent;

/**
 * CreateTeamView
 *
 * View for the team creation screen ("create team").
 * Delegates user actions to the associated controller.
 */
public class CreateTeamFXController extends FXController {

    private CreateTeamController controller;

    // FXML elements
    @FXML private AllBugemonsGridView allBugemonsGridView;

    @FXML private BugemonTeamComponent bugemonsTeamView;

    @FXML private Button launchAutomaticCombat;

    @FXML private Button launchManualCombat;

    /**
     * Loads the create-team FXML layout and initializes button actions.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CreateTeamFXController(CreateTeamController controller) {
        this.controller = controller;
    }

    @FXML
    @Override
    public void initialize() {
        initializeComponents();
    }

    private void initializeComponents() {
        // Load and display all available Bugemons
        List<BugemonDTO> allBugemons = this.controller.getBugemonsDTO();
        showAll(allBugemons);

        // Load and display the player's current team
        List<BugemonDTO> playerTeam = this.controller.getPlayerTeamDTO();
        showTeam(playerTeam);
    }

    /**
     * Displays the player's current team in the team view.
     * @param bugemonList the list of BugemonDTOs representing the player's current team to be
     *         displayed
     */
    @FXML
    public void showTeam(List<BugemonDTO> bugemonList) {
        this.bugemonsTeamView.showTeam(bugemonList);
    }

    /**
     * Displays all available Bugemons in the grid view.
     * @param bugemonList the list of all available Bugemons to be displayed
     */
     @FXML
    public void showAll(List<BugemonDTO> bugemonList) {
        this.allBugemonsGridView.showAll(bugemonList);
    }

    @FXML
    public void onLaunchAutomaticCombat() {
        launchCombat(Window.AUTOMATIC_COMBAT);
    }

    @FXML
    public void onLaunchManualCombat() {
        launchCombat(Window.MANUAL_COMBAT);
    }

    private void launchCombat(Window window) {
        if (this.controller.canStartCombat()) {
            this.metaController.switchTo(Window.MANUAL_COMBAT);
        } else {
            showAlert("Cannot start combat", this.controller.alert());
        }
    }
}
