package ulb.views;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import ulb.common.dto.BugemonDTO;
import ulb.common.dto.LevelUpDTO;
import ulb.models.level_up.Choice;

/**
 * LevelUpView
 *
 * View for the level up screen.
 */
public class LevelUpView extends View {
    @FXML private Label levelUpText;
    @FXML private Button choice1Button;
    @FXML private Button choice2Button;
    @FXML private Button choice3Button;
    @FXML private ImageView bugemonImage;

    public LevelUpView() throws IOException {
        super("/fxml/LevelUp.fxml");
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

    
    /**
     * Set the action to perform when the choice 1 button is clicked.
     * @param action the action to perform
     */
    public void setActionOnChoice1Button(Runnable action) {
        this.choice1Button.setOnAction(e -> action.run());
    }

    /**
     * Set the action to perform when the choice 2 button is clicked.
     * @param action the action to perform
     */
    public void setActionOnChoice2Button(Runnable action) {
        this.choice2Button.setOnAction(e -> action.run());
    }

    /**
     * Set the action to perform when the choice 3 button is clicked.
     * @param action the action to perform
     */
    public void setActionOnChoice3Button(Runnable action) {
        this.choice3Button.setOnAction(e -> action.run());
    }
}
