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
    private Button topRightButton;

    private Listener listener;

    public ActionMenuView() {
        super(Configuration.Paths.Fxml.COMPONENT_ACTION_MENU);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Enables or disables the Switch button depending on whether a voluntary switch is currently allowed.
     *
     * @param canSwitch
     *            {@code true} if the player may switch Bugemons this turn
     */
    public void refresh(boolean canSwitch) {
        this.topRightButton.setDisable(!canSwitch);
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

    /** Callback interface for the main combat action menu. */
    public interface Listener {

        /** Called when the player chooses to attack. */
        void onAttack();

        /** Called when the player chooses to switch the active Bugemon. */
        void onSwitch();

        /** Called when the player opens the inventory. */
        void onInventory();

        /** Called when the player forfeits the combat. */
        void onForfeit();

    }
}
