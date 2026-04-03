package ulb.views;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import ulb.common.dto.BugemonDTO;
import ulb.common.dto.LevelUpDTO;
import ulb.models.level_up.Upgrade;

/**
 * View for the level-up screen.
 *
 * <p>
 * Holds a reference to a {@link LevelUpSession} and reads the current level-up event directly from it in
 * {@link #refresh()}. Dispatches the player's choice through the callback registered via
 * {@link #setOnChooseOption(Consumer)}. Holds no reference to any concrete controller class.
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

    Listener listener;

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
            this.listener.onChooseUpgrade(0);
        });
        this.choice2Button.setOnAction(e -> {
            this.listener.onChooseUpgrade(1);
        });
        this.choice3Button.setOnAction(e -> {
            this.listener.onChooseUpgrade(2);
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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

        List<Upgrade> choices = levelUp.getChoices();
        this.choice1Button.setText(choices.get(0).toString());
        this.choice2Button.setText(choices.get(1).toString());
        this.choice3Button.setText(choices.get(2).toString());
    }

    public interface Listener {

        void onChooseUpgrade(int idx);

    }
}
