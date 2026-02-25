package ulb.views;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ulb.common.BugemonDTO;
import ulb.controllers.CreateTeamController;
import ulb.models.bugemon.Bugemon;

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
    private Group listPane;

    @FXML
    private Group teamPane;

    @FXML
    private Button validateButton;

    @FXML
    private Button loadButton;

    @FXML
    private Button saveButton;

    // Lists of ImageViews
    private List<ImageView> teamBugemons = new java.util.ArrayList<>();
    private List<ImageView> allBugemons = new java.util.ArrayList<>();

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
                        this.controller.addToTeam(dto);
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
                        this.controller.removeFromTeam(dto);
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

    // TODO: code duplication with showAll?
    protected void showTeam(List<BugemonDTO> bugList) {
        assert bugList.size() <= 6 : "A team cannot have more than 6 Bugemons.";
        int sizeList = bugList.size();
        Image unknownImage = new Image("Assets/png/unknown.png");
        for (int i = 0; i < this.teamBugemons.size(); i++) {
            Image img;
            if (i < sizeList) {
                img = new Image(bugList.get(i).getSpriteURL());
            } else {
                img = unknownImage;
            }
            ImageView iv = this.teamBugemons.get(i);
            iv.setImage(img);
        }
    }

    protected void showAll(List<BugemonDTO> bugList) {
        assert bugList.size() <= 20 : "There cannot be more than 20 Bugemons in the list for now.";
        int sizeList = bugList.size();
        Image unknownImage = new Image("/unknown.png");
        for (int i = 0; i < this.allBugemons.size(); i++) {
            Image img;
            if (i < sizeList) {
                img = new Image(bugList.get(i).getSpriteURL());
            } else {
                img = unknownImage;
            }
            ImageView iv = this.allBugemons.get(i);
            iv.setImage(img);
        }
    }
}
