package ulb.views.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import ulb.Configuration;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Efficiency;

/**
 * Generic hover info panel that displays a title and a variable list of info lines. Callers build the content via
 * {@link #show(String, String...)} and dismiss it with {@link #hide()}. Type-based background colouring and an
 * efficiency badge can be layered on top via {@link #setType(BugemonType)} and {@link #setEfficiency(Efficiency)}.
 */
public class HoverInfoView extends ComponentView {

    @FXML
    private Label titleLabel;
    @FXML
    private VBox contentBox;
    @FXML
    private Label efficiencyLabel;

    public HoverInfoView() {
        super(Configuration.Paths.Fxml.COMPONENT_HOVER_INFO);
    }

    /**
     * Populates the panel with the given title and info lines, resets any type colour and efficiency badge, then makes
     * the panel visible.
     *
     * @param title
     *            displayed in bold at the top
     * @param lines
     *            zero or more info lines shown below the title (null/blank lines are skipped)
     */
    public void show(String title, String... lines) {
        this.setType(null);
        this.setEfficiency(null);
        this.titleLabel.setText(title);
        this.contentBox.getChildren().clear();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            Label label = new Label(line);
            label.getStyleClass().add("hover-info-meta");
            label.setWrapText(true);
            label.setMaxWidth(Double.MAX_VALUE);
            this.contentBox.getChildren().add(label);
        }
        this.setVisible(true);
        this.setManaged(true);
    }

    /**
     * Applies a type-based background colour to the panel. Pass {@code null} to restore the default surface colour.
     *
     * @param type
     *            the bugemon type whose colour to apply, or {@code null} to reset
     */
    public void setType(BugemonType type) {
        this.getStyleClass().removeIf(c -> c.startsWith("hover-type-"));
        this.getStyleClass().remove("hover-typed");
        if (type != null) {
            this.getStyleClass().add("hover-type-" + type.toString());
            this.getStyleClass().add("hover-typed");
        }
    }

    /**
     * Shows or hides the efficiency badge. {@code NEUTRAL} and {@code null} hide the badge.
     *
     * @param eff
     *            the efficiency value to display, or {@code null} to hide
     */
    public void setEfficiency(Efficiency eff) {
        this.efficiencyLabel.getStyleClass().removeIf(c -> c.startsWith("hover-info-efficiency"));
        if (eff == null || eff == Efficiency.NEUTRAL) {
            this.efficiencyLabel.setVisible(false);
            this.efficiencyLabel.setManaged(false);
            return;
        }
        if (eff == Efficiency.HIGH) {
            this.efficiencyLabel.setText("Super efficace !");
            this.efficiencyLabel.getStyleClass().add("hover-info-efficiency-high");
        } else {
            this.efficiencyLabel.setText("Peu efficace...");
            this.efficiencyLabel.getStyleClass().add("hover-info-efficiency-low");
        }
        this.efficiencyLabel.setVisible(true);
        this.efficiencyLabel.setManaged(true);
    }

    /** Hides the panel and removes it from the layout flow. */
    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }
}
