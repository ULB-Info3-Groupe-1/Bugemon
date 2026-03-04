package ulb.views;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ulb.common.BugemonDTO;
import ulb.controllers.CreateTeamController;

/**
 * CreateTeamView
 *
 * View for the team creation screen ("create team").
 * Delegates user actions to the associated controller.
 */
public class CreateTeamView extends View {

    private final static String FXML_PATH = "/fxml/CreateTeam.fxml";

    private CreateTeamController controller;

    // FXML elements
    @FXML
    private AllBugemonsGridView allBugemonsGridView;
    @FXML
    private BugemonTeamView bugemonsTeamView;

    @FXML
    private Button validateButton;

    /**
     * Loads the create-team FXML layout and initializes button actions.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CreateTeamView() throws IOException {
        super(FXML_PATH);
        this.controller = null;

        this.allBugemonsGridView.setOnClickCallback((dto) -> {
            this.controller.onBugemonClicked(dto.getId());
        });

        this.validateButton.setOnAction((e) -> this.controller.startCombat());
        this.validateButton.setStyle("-fx-font-size: 24; -fx-background-color: green; -fx-text-fill: white;");
    }

    /**
     * Binds this view to its controller.
     *
     * @param controller controller handling team creation
     */
    public void setController(CreateTeamController controller) {
        this.controller = controller;

        // set selection callback
        this.allBugemonsGridView.setSelectionChecker(b -> this.controller.checkBugemonInTeam(b.getId()));
    }

    public void showTeam(List<BugemonDTO> bugemonList) {
        this.bugemonsTeamView.showTeam(bugemonList);
    }

    public void showAll(List<BugemonDTO> bugemonList) {
        this.allBugemonsGridView.showAll(bugemonList);
    }
}
