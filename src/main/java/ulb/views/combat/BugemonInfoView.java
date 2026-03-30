package ulb.views.combat;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import ulb.common.dto.BugemonDTO;

/**
 * Reusable custom JavaFX component that displays the key information of a
 * single {@link BugemonDTO} in the combat HUD.
 *
 * <p>
 * Shows the Bugemon's name (coloured by type), type label, HP bar, and
 * numeric HP counter.
 * </p>
 */
public class BugemonInfoView extends VBox {
    private static final String FXML_PATH = "/fxml/BugemonInfo.fxml";

    @FXML private Label bugemonName;
    @FXML private Label bugemonType;
    @FXML private ProgressBar bugemonHPBar;
    @FXML private Label bugemonHpLabel;

    public BugemonInfoView() {
        URL url = getClass().getResource(FXML_PATH);
        FXMLLoader loader = new FXMLLoader(url);

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load BugemonInfo.fxml", e);
        }
    }

    public void setBugemonInfo(BugemonDTO bugemon) {
        this.bugemonName.getStyleClass().clear();
        this.bugemonName.getStyleClass().add(bugemon.getType().toString());
        this.bugemonName.setText(bugemon.getName());
        this.bugemonType.setText("(" + bugemon.getType().toString() + ")");
        this.bugemonHPBar.setProgress((double)bugemon.getHp() / bugemon.getMaxHp());
        this.bugemonHpLabel.setText(bugemon.getHp() + " / " + bugemon.getMaxHp());
    }
}
