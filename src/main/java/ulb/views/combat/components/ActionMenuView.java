package ulb.views.combat.components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import ulb.Configuration;
import ulb.views.components.ComponentView;

/**
 * The main action menu displayed at the start of each manual combat turn. Presents four choices: Attack, Switch, Item,
 * Forfeit. Each button dispatches its action through a callback registered from the outside. The Switch button is
 * disabled whenever a voluntary switch is not available, as signalled by {@link #refresh(boolean)}.
 */
public class ActionMenuView extends ComponentView {

    @FXML
    private Button topLeft;
    @FXML
    private Button topRight;
    @FXML
    private Button bottomLeft;
    @FXML
    private Button bottomRight;

    private Listener listener;

    public ActionMenuView() {
        super(Configuration.Paths.FXML.COMPONENT_ACTION_MENU);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void refresh(boolean canSwitch) {
        this.topRight.setDisable(!canSwitch);
    }

    @FXML
    private void onAttackClicked() {
        this.listener.onAttack();
    }

    @FXML
    private void onSwitchClicked() {
        this.listener.onSwitch();
    }

    @FXML
    private void onInventoryClicked() {
        this.listener.onInventory();
    }

    @FXML
    private void onForfeitClicked() {
        this.listener.onForfeit();
    }

    public interface Listener {

        void onAttack();

        void onSwitch();

        void onInventory();

        void onForfeit();

    }
}
