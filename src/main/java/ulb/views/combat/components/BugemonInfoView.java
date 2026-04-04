package ulb.views.combat.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import ulb.common.dto.BugemonDTO;
import ulb.models.bugemon.BugemonType;
import ulb.views.components.ComponentView;

/** Reusable combat HUD component displaying name, level, HP bar, and XP bar for a single {@link BugemonDTO}. */
public class BugemonInfoView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/BugemonInfo.fxml";

    @FXML
    private Label bugemonName;
    @FXML
    private Label bugemonLevel;
    @FXML
    private ProgressBar bugemonHPBar;
    @FXML
    private ProgressBar bugemonXpBar;;
    @FXML
    private Label bugemonHpLabel;

    public BugemonInfoView() {
        super(FXML_PATH);
    }

    /** Refreshes all displayed fields from the given {@link BugemonDTO} and applies the type style class. */
    public void setBugemonInfo(BugemonDTO bugemon) {
        for (BugemonType type : BugemonType.values()) {
            this.getStyleClass().remove(type.toString());
        }
        this.getStyleClass().add(bugemon.getType().toString());
        this.bugemonName.setText(bugemon.getName());
        this.bugemonLevel.setText("Lv." + bugemon.getLevel());
        this.bugemonHPBar.setProgress((double) bugemon.getHp() / bugemon.getMaxHp());
        this.bugemonXpBar.setProgress(bugemon.getXpProgress());
        this.bugemonHpLabel.setText(bugemon.getHp() + " / " + bugemon.getMaxHp() + " HP");
    }
}
