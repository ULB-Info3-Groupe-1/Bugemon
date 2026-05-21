package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import ulb.Configuration;
import ulb.models.player.BonusStats;

/**
 * View for the level-up screen. Holds a reference to the displayed LevelUp.
 */
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

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onChoice0Clicked() {
    }

    @FXML
    private void onChoice1Clicked() {
    }

    @FXML
    private void onChoice2Clicked() {
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.LEVEL_UP_VIEW;
    }

    @Override
    public void refresh() {
    }

    public interface Listener {

        void onBonusChosen(BonusStats bonus);

    }
}
