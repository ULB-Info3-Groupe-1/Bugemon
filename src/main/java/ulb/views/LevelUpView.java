package ulb.views;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import ulb.Configuration;
import ulb.common.dto.BugemonDTO;
import ulb.models.level_up.LevelUp;

/** View for the level-up screen, presenting three upgrade choices for the player to pick. */
public class LevelUpView extends View {

    @FXML
    private Label levelUpText;
    @FXML
    private Button choice0Button;
    @FXML
    private Button choice1Button;
    @FXML
    private Button choice2Button;
    @FXML
    private ImageView bugemonImage;

    private LevelUp levelUp;

    private Listener listener;

    public void setLevelUp(LevelUp levelUp) {
        this.levelUp = levelUp;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onChoice0Clicked() {
        this.listener.onUpgradeChosen(0);
    }

    @FXML
    private void onChoice1Clicked() {
        this.listener.onUpgradeChosen(1);
    }

    @FXML
    private void onChoice2Clicked() {
        this.listener.onUpgradeChosen(2);
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.LEVEL_UP_VIEW;
    }

    @Override
    public void refresh() {
        if (this.levelUp == null) {
            return;
        }

        BugemonDTO bugemon = this.levelUp.getBugemon();

        File file = new File(Configuration.Paths.SPRITES + bugemon.getSpriteURL());
        this.bugemonImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
        this.levelUpText.setText(bugemon.getName() + " vient juste de passer au niveau " + bugemon.getLevel() + " !");

        this.choice0Button.setText(this.levelUp.get(0).toString());
        this.choice1Button.setText(this.levelUp.get(1).toString());
        this.choice2Button.setText(this.levelUp.get(2).toString());
    }

    public interface Listener {

        void onUpgradeChosen(int optionIdx);

    }
}
