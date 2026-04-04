package ulb.views.combat.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.views.components.ComponentView;

/**
 * The main action menu displayed at the start of each manual combat turn.
 *
 * <p>
 * Presents four choices: Attack, Switch, Item, Surrender. Each button dispatches its action through a callback
 * registered from the outside. The Switch button is disabled whenever a voluntary switch is not available, as signalled
 * by {@link #refresh(boolean)}.
 * </p>
 */
public class ActionMenuView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/ActionMenu.fxml";

    @FXML
    private Button topLeft;
    @FXML
    private Button topRight;
    @FXML
    private Button bottomLeft;
    @FXML
    private Button bottomRight;

    private Runnable onAttack;
    private Runnable onSwitch;
    private Runnable onInventory;
    private Runnable onSurrender;

    public ActionMenuView() {
        super(FXML_PATH);
    }

    /** Enables or disables the Switch button based on whether a voluntary switch is allowed. */
    public void refresh(boolean canSwitch) {
        this.topRight.setDisable(!canSwitch);
    }

    public void setOnAttack(Runnable callback) {
        this.onAttack = callback;
    }

    public void setOnSwitch(Runnable callback) {
        this.onSwitch = callback;
    }

    public void setOnInventory(Runnable callback) {
        this.onInventory = callback;
    }

    public void setOnSurrender(Runnable callback) {
        this.onSurrender = callback;
    }

    @FXML
    private void onAttackClicked() {
        if (this.onAttack != null) {
            this.onAttack.run();
        }
    }

    @FXML
    private void onSwitchClicked() {
        if (this.onSwitch != null) {
            this.onSwitch.run();
        }
    }

    @FXML
    private void onInventoryClicked() {
        if (this.onInventory != null) {
            this.onInventory.run();
        }
    }

    @FXML
    private void onSurrenderClicked() {
        if (this.onSurrender != null) {
            this.onSurrender.run();
        }
    }
}
