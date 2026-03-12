package ulb.views;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.common.dto.BugemonDTO;

/**
 * CreateTeamView
 *
 * View for the team creation screen ("create team").
 * Delegates user actions to the associated controller.
 */
public class CreateTeamView extends View {
    private static final String FXML_PATH = "/fxml/CreateTeam.fxml";

    // FXML elements
    @FXML private AllBugemonsGridView allBugemonsGridView;

    @FXML private BugemonTeamView bugemonsTeamView;

    @FXML private Button launchAutomaticCombat;

    @FXML private Button launchManualCombat;

    /**
     * Loads the create-team FXML layout and initializes button actions.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CreateTeamView() throws IOException {
        super(FXML_PATH);
    }

    /**
     * Sets the callback to be invoked when a Bugemon is clicked in the grid view.
     * @param consumer the Consumer that will handle the clicked BugemonDTO
     */
    public void setOnBugemonClicked(Consumer<BugemonDTO> consumer) {
        this.allBugemonsGridView.setOnClickCallback(
                dto -> { consumer.accept(dto); });
    }

    /**
     * Sets the callback used to check if a Bugemon should be marked as selected in the grid view.
     * @param function the Function that takes a BugemonDTO and returns true if it should be marked as selected
     */
    public void setCheckSelectionChecker(Function<BugemonDTO, Boolean> function) {
        this.allBugemonsGridView.setSelectionChecker(function);
    }

    /**
     * Sets the action to be performed when the "Launch Manual Combat" button is clicked.
     * @param action the Runnable action to execute when the button is clicked
     */
    public void setActionLaunchManualCombat(Runnable action) {
        this.launchManualCombat.setOnAction(e -> action.run());
    }

    /**
     * Sets the action to be performed when the "Launch Automatic Combat" button is clicked.
     * @param action the Runnable action to execute when the button is clicked
     */
    public void setActionLaunchAutomaticCombat(Runnable action) {
        this.launchAutomaticCombat.setOnAction(e -> action.run());
    }

    /**
     * Displays the player's current team in the team view.
     * @param bugemonList the list of BugemonDTOs representing the player's current team to be
     *         displayed
     */
    public void showTeam(List<BugemonDTO> bugemonList) {
        this.bugemonsTeamView.showTeam(bugemonList);
    }

    /**
     * Displays all available Bugemons in the grid view.
     * @param bugemonList the list of all available Bugemons to be displayed
     */
    public void showAll(List<BugemonDTO> bugemonList) {
        this.allBugemonsGridView.showAll(bugemonList);
    }
}
