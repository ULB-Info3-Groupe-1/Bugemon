package ulb.views.combat.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import ulb.common.dto.BugemonDTO;
import ulb.views.components.ComponentView;

/**
 * Reusable custom JavaFX component that displays the key information of a single {@link BugemonDTO} in the combat HUD.
 *
 * <p>
 * Shows the Bugemon's name (coloured by type), type label, HP bar, and numeric HP counter.
 * </p>
 */
public class BugemonInfoView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/BugemonInfo.fxml";

    @FXML
    private Label bugemonName;
    @FXML
    private Label bugemonType;
    @FXML
    private ProgressBar bugemonHPBar;
    @FXML
    private Label bugemonHpLabel;

    public BugemonInfoView() {
        super(FXML_PATH);
    }

    /** Updates all displayed fields from the given {@link BugemonDTO}. */
    public void setBugemonInfo(BugemonDTO bugemon) {
        this.bugemonName.getStyleClass().clear();
        this.bugemonName.getStyleClass().add(bugemon.getType().toString());
        this.bugemonName.setText(bugemon.getName());
        this.bugemonType.setText("(" + bugemon.getType().toString() + ")");
        this.bugemonHPBar.setProgress((double) bugemon.getHp() / bugemon.getMaxHp());
        this.bugemonHpLabel.setText(bugemon.getHp() + " / " + bugemon.getMaxHp());
    }
}
