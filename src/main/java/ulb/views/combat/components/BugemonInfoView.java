package ulb.views.combat.components;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

import ulb.Configuration;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.CombatBugemon;
import ulb.views.components.ComponentView;

/**
 * Reusable combat HUD component displaying name, level, HP bar, and XP bar for a single {@link CombatBugemon}. The
 * component's root node also receives a CSS class matching the Bugemon's {@link ElementType} for type-coloured theming.
 *
 * <p>
 * The HP bar is colour-coded by remaining fraction (green / amber / red) and both the bar fill and the numeric label
 * animate smoothly when HP changes, giving the classic "draining gauge" feel.
 */
public class BugemonInfoView extends ComponentView {

    private static final String HP_HIGH = "hp-high";
    private static final String HP_MEDIUM = "hp-medium";
    private static final String HP_LOW = "hp-low";
    private static final double HP_HIGH_THRESHOLD = 0.5;
    private static final double HP_MEDIUM_THRESHOLD = 0.2;
    private static final Duration HP_ANIMATION_DURATION = Duration.millis(450);

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

    private int maxHp;
    private Timeline hpAnimation;

    public BugemonInfoView() {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_INFO);
        // Keep the numeric label in sync with the (possibly animated) bar fill.
        this.bugemonHPBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (this.maxHp > 0) {
                int shown = (int) Math.round(newValue.doubleValue() * this.maxHp);
                this.bugemonHpLabel.setText(shown + " / " + this.maxHp + " PV");
            }
        });
    }

    /**
     * Refreshes all displayed fields from the given {@link CombatBugemon} and applies the corresponding element-type
     * style class to the component root. The HP bar is set instantly (no animation) since this reflects a freshly shown
     * Bugemon rather than an in-combat HP change.
     *
     * @param bugemon
     *            the Bugemon whose data should be reflected in the HUD
     */
    public void setBugemonInfo(CombatBugemon bugemon) {
        for (ElementType type : ElementType.values()) {
            this.getStyleClass().remove(type.toString());
        }
        this.getStyleClass().add(bugemon.getType().toString());
        this.maxHp = bugemon.getMaxHp();
        this.bugemonName.setText(bugemon.getName());
        this.bugemonLevel.setText("Lv." + bugemon.getLevel());
        this.bugemonXpBar.setProgress(bugemon.getXpProgress());
        this.applyHpRatio((double) bugemon.getCurrentHp() / bugemon.getMaxHp(), false);
        this.bugemonHpLabel.setText(bugemon.getCurrentHp() + " / " + bugemon.getMaxHp() + " PV");
    }

    /**
     * Updates only the HP bar and label without re-rendering the full HUD, animating the change; used after damage or
     * healing events.
     *
     * @param currentHp
     *            the Bugemon's remaining HP
     * @param maximumHp
     *            the Bugemon's maximum HP
     */
    public void setHp(int currentHp, int maximumHp) {
        this.maxHp = maximumHp;
        this.applyHpRatio((double) currentHp / maximumHp, true);
    }

    /**
     * Applies the HP fraction to the bar: swaps the colour class by threshold and either snaps or animates the fill.
     * When animating, the numeric label follows the bar via the progress listener.
     */
    private void applyHpRatio(double ratio, boolean animate) {
        double clamped = Math.clamp(ratio, 0.0, 1.0);

        this.bugemonHPBar.getStyleClass().removeAll(HP_HIGH, HP_MEDIUM, HP_LOW);
        String colourClass = clamped > HP_HIGH_THRESHOLD ? HP_HIGH : clamped > HP_MEDIUM_THRESHOLD ? HP_MEDIUM : HP_LOW;
        this.bugemonHPBar.getStyleClass().add(colourClass);

        if (this.hpAnimation != null) {
            this.hpAnimation.stop();
        }
        if (animate) {
            this.hpAnimation = new Timeline(new KeyFrame(HP_ANIMATION_DURATION,
                    new KeyValue(this.bugemonHPBar.progressProperty(), clamped, Interpolator.EASE_BOTH)));
            this.hpAnimation.play();
        } else {
            this.bugemonHPBar.setProgress(clamped);
        }
    }
}
