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
import ulb.models.level_up.Choice;
import ulb.models.level_up.LevelUpSession;

/**
 * View for the level-up screen.
 *
 * <p>
 * Holds a reference to a {@link LevelUpSession} and reads the current level-up
 * event directly from it in {@link #refresh()}. Dispatches the player's choice
 * through the callback registered via {@link #setOnChooseOption(Consumer)}.
 * Holds no reference to any concrete controller class.
 * </p>
 */
public class LevelUpView extends View {
    @FXML private Label levelUpText;
    @FXML private Button choice1Button;
    @FXML private Button choice2Button;
    @FXML private Button choice3Button;
    @FXML private ImageView bugemonImage;

    private LevelUpSession session;
    private Consumer<Integer> onChooseOption;

    /**
     * Loads the level-up FXML layout and wires each choice button to fire the
     * registered callback with its zero-based index (0, 1, or 2).
     *
     * @throws IOException if the FXML resource cannot be loaded.
     */
    public LevelUpView() throws IOException {
        super("/fxml/LevelUp.fxml");
        this.choice1Button.setOnAction(e -> {
            if (onChooseOption != null)
                onChooseOption.accept(0);
        });
        this.choice2Button.setOnAction(e -> {
            if (onChooseOption != null)
                onChooseOption.accept(1);
        });
        this.choice3Button.setOnAction(e -> {
            if (onChooseOption != null)
                onChooseOption.accept(2);
        });
    }

    /** Gives the view a reference to the level-up session model it should read from. */
    public void setSession(LevelUpSession session) {
        this.session = session;
    }

    /** Registers the callback invoked when the player selects a stat-upgrade option. */
    public void setOnChooseOption(Consumer<Integer> callback) {
        this.onChooseOption = callback;
    }

    @Override
    public void refresh() {
        if (session == null || !session.isStarted())
            return;

        LevelUpDTO levelUp = session.getCurrent();
        BugemonDTO bugemon = levelUp.getBugemon();

        bugemonImage.setImage(new Image(bugemon.getSpriteURL()));
        levelUpText.setText(bugemon.getName() + " vient juste de passer au niveau "
                            + bugemon.getLevel() + " !");

        List<Choice> choices = levelUp.getChoices();
        choice1Button.setText(choices.get(0).toString());
        choice2Button.setText(choices.get(1).toString());
        choice3Button.setText(choices.get(2).toString());
    }
}
