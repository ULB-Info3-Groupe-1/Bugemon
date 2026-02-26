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

/**
 * CreateTeamView
 *
 * View for the team creation screen ("create team").
 * Delegates user actions to the associated controller.
 */
public class CreateTeamView extends View {

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
        super("/fxml/CreateTeam.fxml");
        this.controller = null;
        for (Node node : listPane.getChildren()) {
            if (node instanceof ImageView imageView) {
                this.allBugemons.add(imageView);

                imageView.setOnMouseClicked(e -> {
                    BugemonDTO dto = (BugemonDTO) imageView.getUserData();
                    if (dto != null) {
                        this.controller.addToTeam(dto.getId());
                    }
                });
            }
        }

        for (Node node : teamPane.getChildren()) {
            if (node instanceof ImageView imageView) {
                this.teamBugemons.add(imageView);

                imageView.setOnMouseClicked(e -> {
                    BugemonDTO dto = (BugemonDTO) imageView.getUserData();
                    if (dto != null) {
                        this.controller.removeFromTeam(dto.getId());
                    }
                });
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

    @Override
    protected String getTitle() {
        return "Create Team";
    }

    protected void wrongSelect(ImageView iv) {
        iv.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    public void showTeam(List<BugemonDTO> bugemonList) {
        int MAX_TEAM_DISPLAY_SIZE = 6;
        assert bugemonList.size() <= MAX_TEAM_DISPLAY_SIZE : "Cannot display teams bigger than 6 Bugemons.";
        for (int idx = 0; idx < this.teamBugemons.size(); idx++) {
            BugemonDTO bugemonDTO = bugemonList.get(idx);

            ImageView iv = this.teamBugemons.get(idx);
            Image img = (bugemonDTO != null) ? new Image(bugemonList.get(idx).getSpriteURL()) : this.UNKNOWN_IMAGE;
            iv.setImage(img);
        }
    }

    public void showAll(List<BugemonDTO> bugemonList) {
        int MAX_ALL_BUGEMONS_DISPLAY_SIZE = 20;
        assert bugemonList.size() <= MAX_ALL_BUGEMONS_DISPLAY_SIZE
                : "There cannot be more than 20 Bugemons.";
        for (int idx = 0; idx < this.allBugemons.size(); idx++) {

            Image img = (idx < bugemonList.size()) ? new Image(bugemonList.get(idx).getSpriteURL()) : this.UNKNOWN_IMAGE;
            ImageView iv = this.allBugemons.get(idx);
            iv.setImage(img);
        }
    }
}
