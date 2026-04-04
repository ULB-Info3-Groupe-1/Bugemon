package ulb.views;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import ulb.common.dto.BugemonDTO;
import ulb.common.dto.LevelUpDTO;
import ulb.models.level_up.LevelUpSession;

/**
 * View for the level-up screen.
 *
 * <p>
 * Holds a reference to a {@link LevelUpSession} and reads the current level-up event directly from it in
 * {@link #refresh()}. Dispatches the player's choice through the callback registered via
 * {@link #setOnUpgradeChosen(Consumer)}. Holds no reference to any concrete controller class.
 * </p>
 */
public class LevelUpView extends View {
    private final String fxmlPath = "/fxml/LevelUp.fxml";

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

    private LevelUpSession session;

    private Listener listener;

    /**
     * Loads the level-up FXML layout and wires each choice button to fire the registered callback with its zero-based
     * index (0, 1, or 2).
     *
     * @throws IOException
     *             if the FXML resource cannot be loaded.
     */
    public LevelUpView() {
    }

    /** Gives the view a reference to the level-up session model it should read from. */
    public void setSession(LevelUpSession session) {
        this.session = session;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onChoice1Clicked() {
        this.listener.onUpgradeChosen(0);
    }

    @FXML
    private void onChoice2Clicked() {
        this.listener.onUpgradeChosen(1);
    }

    @FXML
    private void onChoice3Clicked() {
        this.listener.onUpgradeChosen(2);
    }

    @Override
    public String getPath() {
        return this.fxmlPath;
    }

    @Override
    public void refresh() {
        if (this.session == null || !this.session.isStarted()) {
            return;
        }
        LevelUpDTO levelUp = this.session.getCurrent();
        BugemonDTO bugemon = levelUp.getBugemon();

        File file = new File("resources/sprites/" + bugemon.getSpriteURL());
        this.bugemonImage.setImage(new Image(file.toURI().toString(), 256, 256, true, false));
        this.levelUpText.setText(bugemon.getName() + " vient juste de passer au niveau " + bugemon.getLevel() + " !");

        this.choice1Button.setText(levelUp.get(0).toString());
        this.choice2Button.setText(levelUp.get(1).toString());
        this.choice3Button.setText(levelUp.get(2).toString());
    }

    public interface Listener {
        void onUpgradeChosen(int optionIdx);
    }
}
