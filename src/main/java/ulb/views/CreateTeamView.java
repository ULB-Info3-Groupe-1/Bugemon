package ulb.views;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ulb.common.BugemonDTO;
import ulb.controllers.CreateTeamController;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;

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
    private AnchorPane rootPane;
    @FXML
    private Pane listPane;

    @FXML
    private Pane teamPane;

    @FXML
    private Button validateButton;

    @FXML
    private Button loadButton;

    @FXML
    private Button saveButton;

    // Lists of ImageViews
    private List<ImageView> teamBugemons = new java.util.ArrayList<>();
    private List<ImageView> allBugemons = new java.util.ArrayList<>();

    // Unknown image if no Bugemon available
    private final Image UNKNOWN_IMAGE = new Image("/png/unknown.png");

    /**
     * Loads the create-team FXML layout and initializes button actions.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public CreateTeamView() throws IOException {
        super(FXML_PATH);
        this.controller = null;
        for (Node node : listPane.getChildren()) {
            if (node instanceof ImageView imageView) {
                this.allBugemons.add(imageView);

                imageView.setOnMouseClicked(e -> {
                    BugemonDTO dto = (BugemonDTO) imageView.getUserData();
                    if (dto != null) {
                        try {
                            this.controller.onBugemonClicked(dto.getId());
                        } catch (BugemonAlreadyExistsException exception) {
                        }
                    }
                });
            }
        }

        for (Node node : teamPane.getChildren()) {
            if (node instanceof ImageView imageView) {
                this.teamBugemons.add(imageView);
            }
        }

        this.validateButton.setOnAction((e) -> this.controller.startCombat());
    }

    /**
     * Binds this view to its controller.
     *
     * @param controller controller handling team creation
     */
    public void setController(CreateTeamController controller) {
        this.controller = controller;
    }

    private void select(ImageView iv) {
        iv.setStyle("-fx-effect: dropshadow(three-pass-box, red, 5, 0.9, 0, 0);");
    }

    private void unselect(ImageView iv) {
        iv.setStyle("");
    }

    public void showTeam(List<BugemonDTO> bugemonList) {
        int MAX_TEAM_DISPLAY_SIZE = 6;
        assert bugemonList.size() <= MAX_TEAM_DISPLAY_SIZE : "Cannot display teams bigger than 6 Bugemons.";
        for (int idx = 0; idx < this.teamBugemons.size(); idx++) {

            BugemonDTO bugemon = bugemonList.get(idx);
            ImageView iv = this.teamBugemons.get(idx);
            iv.setScaleX(0.75);
            iv.setScaleY(0.75);

            Image img = (bugemon != null)
                    ? new Image("/png/" + bugemon.getSpriteURL()) // TODO : Retirer le /png/ 
                    : this.UNKNOWN_IMAGE;

            iv.setImage(img);
            iv.setUserData(bugemon);
        }
    }

    public void showAll(List<BugemonDTO> bugemonList) {
        int MAX_ALL_BUGEMONS_DISPLAY_SIZE = 20;
        assert bugemonList.size() <= MAX_ALL_BUGEMONS_DISPLAY_SIZE
                : "There cannot be more than 20 Bugemons.";
        for (int idx = 0; idx < this.allBugemons.size(); idx++) {

            BugemonDTO bugemon = (idx < bugemonList.size()) ? bugemonList.get(idx) : null;
            ImageView iv = this.allBugemons.get(idx);
            iv.setScaleX(0.75);
            iv.setScaleY(0.75);

            Image img = (bugemon != null)
                    ? new Image("/png/" + bugemon.getSpriteURL()) // TODO : Retirer le /png/ 
                    : this.UNKNOWN_IMAGE;

            iv.setImage(img);
            iv.setUserData(bugemon);

            if (bugemon != null && this.controller.checkBugemonInTeam(bugemon.getId())) {
                this.select(iv);
            } else {
                this.unselect(iv);
            }
        }
    }
}
