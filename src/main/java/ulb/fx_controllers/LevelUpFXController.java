package ulb.fx_controllers;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import ulb.common.dto.BugemonDTO;
import ulb.common.dto.LevelUpDTO;
import ulb.controllers.LevelUpController;
import ulb.models.level_up.Choice;

/**
 * LevelUpView
 *
 * View for the level up screen.
 */
public class LevelUpFXController extends FXController {
    @FXML private Label levelUpText;
    @FXML private Button choice1Button;
    @FXML private Button choice2Button;
    @FXML private Button choice3Button;
    @FXML private ImageView bugemonImage;

    private LevelUpController controller;

    public LevelUpFXController(LevelUpController controller) {
        this.controller = controller;

        this.choice1Button.setOnAction((e) -> this.controller.chooseOption(0));
        this.choice2Button.setOnAction((e) -> this.controller.chooseOption(1));
        this.choice3Button.setOnAction((e) -> this.controller.chooseOption(2));
    }

    public void setLevelUp(LevelUpDTO levelUp) {
        BugemonDTO bugemon = levelUp.getBugemon();

        Image sprite = new Image(bugemon.getSpriteURL());

        StringBuilder texte = new StringBuilder();
        texte.append(bugemon.getName());
        texte.append(" vient juste de passer au niveau ");
        texte.append(bugemon.getLevel());
        texte.append(" !");

        levelUpText.setText(texte.toString());
        bugemonImage.setImage(sprite);

        List<Choice> choices = levelUp.getChoices();
        this.choice1Button.setText(choices.get(0).toString());
        this.choice2Button.setText(choices.get(1).toString());
        this.choice3Button.setText(choices.get(2).toString());
    }
}
