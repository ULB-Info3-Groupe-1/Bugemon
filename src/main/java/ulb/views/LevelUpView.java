package ulb.views;

import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import ulb.Configuration;
import ulb.models.player.BonusStats;

/**
 * View shown after a Bugemon levels up. Displays the Bugemon's sprite and name alongside three bonus-stat buttons; the
 * player picks one to permanently apply. All choices are forwarded through {@link Listener}.
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
    private List<BonusStats> currentOptions;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Populates the view with the Bugemon's new level information and the three bonus options to choose from.
     *
     * @param bugemonName
     *            name of the levelled-up Bugemon
     * @param newLevel
     *            the new level reached
     * @param spritePath
     *            relative sprite path appended to the configured sprites directory, or {@code null} to skip loading
     * @param options
     *            exactly three {@link BonusStats} choices presented as buttons
     */
    public void displayLevelUpOptions(String bugemonName, int newLevel, String spritePath, List<BonusStats> options) {
        this.currentOptions = options;
        this.levelUpText.setText(bugemonName + " vient juste de passer au niveau " + newLevel + " !");

        if (spritePath != null) {
            File spriteFile = new File(Configuration.Paths.SPRITES + spritePath);
            this.bugemonImage.setImage(new Image(spriteFile.toURI().toString()));
        }

        this.choice0Button.setText(this.formatBonus(options.get(0)));
        this.choice1Button.setText(this.formatBonus(options.get(1)));
        this.choice2Button.setText(this.formatBonus(options.get(2)));
    }

    private String formatBonus(BonusStats b) {
        return String.format("+%d PV, +%d Attack, +%d Defense, +%d Initiative", b.getBonusHp(), b.getBonusAttack(),
                b.getBonusDefense(), b.getBonusInitiative());
    }

    @FXML
    private void onChoice0Clicked() {
        if (this.listener != null) {
            this.listener.onBonusChosen(this.currentOptions.get(0));
        }
    }

    @FXML
    private void onChoice1Clicked() {
        if (this.listener != null) {
            this.listener.onBonusChosen(this.currentOptions.get(1));
        }
    }

    @FXML
    private void onChoice2Clicked() {
        if (this.listener != null) {
            this.listener.onBonusChosen(this.currentOptions.get(2));
        }
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.LEVEL_UP_VIEW;
    }

    @Override
    public void refresh() {
        // Nothing to refresh
    }

    /** Callback interface for the level-up bonus selection. */
    public interface Listener {
        /**
         * Called when the player selects a stat bonus.
         *
         * @param bonus
         *            the chosen bonus to apply permanently
         */
        void onBonusChosen(BonusStats bonus);
    }
}
