package ulb.views;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ulb.common.BugemonDTO;
import ulb.common.LevelUpDTO;
import ulb.controllers.LevelUpController;

/**
 * LevelUpView
 *
 * View for the level up screen.
 */
public class LevelUpView extends View {

    @FXML
    private Label levelUpText;
    @FXML
    private Button choice1Button;
    @FXML
    private Button choice2Button;
    @FXML
    private Button choice3Button;
    @FXML
    private ImageView bugemonImage;

    private LevelUpController controller;

    public LevelUpView() throws IOException {
        super("/fxml/LevelUp.fxml");
        this.controller = null;

        this.choice1Button.setOnAction((e) -> this.controller.chooseOption(1));
        this.choice2Button.setOnAction((e) -> this.controller.chooseOption(2));
        this.choice2Button.setOnAction((e) -> this.controller.chooseOption(3));
    }

    public void setLevelUp(LevelUpDTO levelUp) {
        BugemonDTO bugemon = levelUp.getBugemon();

        Image sprite = new Image(bugemon.getSpriteURL());

        StringBuilder texte = new StringBuilder();
        texte.append(bugemon.getName());
        texte.append(" a atteint le niveau ");
        texte.append(bugemon.getLevel());

        levelUpText.setText(texte.toString());
        bugemonImage.setImage(sprite);
    }

    /**
     * Binds Level up view to its controller.
     *
     * @param controller controller handling level up
     */
    public void setController(LevelUpController controller) {
        this.controller = controller;
    }

}
