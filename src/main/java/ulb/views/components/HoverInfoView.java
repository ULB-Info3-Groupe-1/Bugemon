package ulb.views.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.damage.Efficiency;
import ulb.models.item.Item;

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
     * Populates the panel with attack details and a type-matchup efficiency badge, then makes it visible.
     *
     * @param attack
     *            the attack to preview
     * @param efficiency
     *            the type-matchup result against the current opponent; {@link Efficiency#NORMAL} hides the badge
     */
    public void show(Attack attack, Efficiency efficiency) {
        this.setType(attack.type());
        this.setEfficiency(efficiency);
        StringBuilder sb = new StringBuilder();
        sb.append("Type : ").append(attack.type()).append("    Puissance : ").append(attack.power());
        String desc = attack.description();
        if (desc != null && !desc.isBlank()) {
            sb.append('\n').append(desc);
        }
        this.titleLabel.setText(attack.name());
        this.contentBox.getChildren().clear();
        this.contentBox.getChildren().add(new Label(sb.toString()));
        this.setVisible(true);
        this.setManaged(true);
    }

    /**
     * Populates the panel with item details (name and description), then makes it visible. No type colouring or
     * efficiency badge is applied.
     *
     * @param item
     *            the item to preview
     */
    public void show(Item item) {
        this.setType(null);
        this.setEfficiency(null);
        String desc = item.description();
        this.titleLabel.setText(item.name());
        this.contentBox.getChildren().clear();
        if (desc != null && !desc.isBlank()) {
            this.contentBox.getChildren().add(new Label(desc));
        }
        this.setVisible(true);
        this.setManaged(true);
    }

    /** Hides the panel and removes it from the layout flow. */
    public void hide() {
        this.setVisible(false);
        this.setManaged(false);
    }

    /**
     * Applies a type-based background colour to the panel. Pass {@code null} to restore the default surface colour.
     *
     * @param type
     *            the bugemon type whose colour to apply, or {@code null} to reset
     */
    private void setType(ElementType type) {
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
    private void setEfficiency(Efficiency eff) {
        this.efficiencyLabel.getStyleClass().removeIf(c -> c.startsWith("hover-info-efficiency"));
        if (eff == null || eff == Efficiency.NORMAL) {
            this.efficiencyLabel.setVisible(false);
            this.efficiencyLabel.setManaged(false);
            return;
        }
        if (eff == Efficiency.SUPER_EFFICIENT) {
            this.efficiencyLabel.setText("Super efficace !");
            this.efficiencyLabel.getStyleClass().add("hover-info-efficiency-high");
        } else {
            this.efficiencyLabel.setText("Peu efficace...");
            this.efficiencyLabel.getStyleClass().add("hover-info-efficiency-low");
        }
        this.efficiencyLabel.setVisible(true);
        this.efficiencyLabel.setManaged(true);
    }
}
