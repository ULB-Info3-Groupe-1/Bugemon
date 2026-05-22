package ulb.views.combat.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import ulb.Configuration;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.CombatBugemon;
import ulb.views.components.ComponentView;

/**
 * Reusable combat HUD component displaying name, level, HP bar, and XP bar for a single {@link CombatBugemon}. The
 * component's root node also receives a CSS class matching the Bugemon's {@link ElementType} for type-coloured theming.
 */
public class BugemonInfoView extends ComponentView {

    @FXML
    private Label bugemonName;
    @FXML
    private Label bugemonLevel;
    @FXML
    private ProgressBar bugemonHPBar;
    @FXML
    private ProgressBar bugemonXpBar;
    @FXML
    private Label bugemonHpLabel;

    public BugemonInfoView() {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_INFO);
    }

    /**
     * Refreshes all displayed fields from the given {@link CombatBugemon} and applies the corresponding element-type
     * style class to the component root.
     *
     * @param bugemon
     *            the Bugemon whose data should be reflected in the HUD
     */
    public void setBugemonInfo(CombatBugemon bugemon) {
        for (ElementType type : ElementType.values()) {
            this.getStyleClass().remove(type.toString());
        }
        this.getStyleClass().add(bugemon.getType().toString());
        this.bugemonName.setText(bugemon.getName());
        this.bugemonLevel.setText("Lv." + bugemon.getLevel());
        this.bugemonHPBar.setProgress((double) bugemon.getCurrentHp() / bugemon.getMaxHp());
        this.bugemonXpBar.setProgress(bugemon.getXpProgress());
        this.bugemonHpLabel.setText(bugemon.getCurrentHp() + " / " + bugemon.getMaxHp() + " PV");
    }

    /**
     * Updates only the HP bar and label without re-rendering the full HUD; used after damage or healing events.
     *
     * @param currentHp
     *            the Bugemon's remaining HP
     * @param maxHp
     *            the Bugemon's maximum HP
     */
    public void setHp(int currentHp, int maxHp) {
        this.bugemonHPBar.setProgress((double) currentHp / maxHp);
        this.bugemonHpLabel.setText(currentHp + " / " + maxHp + " PV");
    }
}
