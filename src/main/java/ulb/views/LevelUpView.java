package ulb.views;

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
    private Consumer<Integer> onChooseOption;

    /**
     * Loads the level-up FXML layout and wires each choice button to fire the registered callback with its zero-based
     * index (0, 1, or 2).
     *
     * @throws IOException
     *             if the FXML resource cannot be loaded.
     */
    public LevelUpView() throws IOException {
        super("/fxml/LevelUp.fxml");
        this.choice1Button.setOnAction(e -> {
            if (this.onChooseOption != null) {
                this.onChooseOption.accept(0);
            }
        });
        this.choice2Button.setOnAction(e -> {
            if (this.onChooseOption != null) {
                this.onChooseOption.accept(1);
            }
        });
        this.choice3Button.setOnAction(e -> {
            if (this.onChooseOption != null) {
                this.onChooseOption.accept(2);
            }
        });
    }

    /** Gives the view a reference to the level-up session model it should read from. */
    public void setSession(LevelUpSession session) {
        this.session = session;
    }

    /** Registers the callback invoked when the player selects a stat-upgrade option. */
    public void setOnUpgradeChosen(Consumer<Integer> callback) {
        this.onChooseOption = callback;
    }

    @Override
    public void refresh() {
        if (this.session == null || !this.session.isStarted()) {
            return;
        }
        LevelUpDTO levelUp = this.session.getCurrent();
        BugemonDTO bugemon = levelUp.getBugemon();

        this.bugemonImage.setImage(new Image(bugemon.getSpriteURL(), 256, 256, true, false));
        this.levelUpText.setText(bugemon.getName() + " vient juste de passer au niveau " + bugemon.getLevel() + " !");

        this.choice1Button.setText(levelUp.get(0).toString());
        this.choice2Button.setText(levelUp.get(1).toString());
        this.choice3Button.setText(levelUp.get(2).toString());
    }
}
