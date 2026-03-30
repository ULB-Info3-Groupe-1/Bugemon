package ulb.views.combat;

/**
 * The main action menu displayed at the start of each manual combat turn.
 *
 * <p>
 * Presents four choices: Attack, Switch, Item, Surrender. Each button
 * dispatches its action through a callback registered from the outside.
 * The Switch button is disabled whenever a voluntary switch is not available,
 * as signalled by {@link #refresh(boolean)}.
 * </p>
 *
 * <p>
 * Holds no reference to any controller class.
 * </p>
 */
public class MainActionMenu extends ActionMenuView {
    public MainActionMenu() {
        super();
        this.action1.setText("Attaque");
        this.action1.getStyleClass().setAll("btn", "btn-danger");
        this.action2.setText("Changer de Bugémon");
        this.action2.getStyleClass().setAll("btn", "btn-action-blue");
        this.action3.setText("Utiliser un objet");
        this.action3.getStyleClass().setAll("btn", "btn-warning");
        this.action4.setText("Abandonner");
    }

    /** Enables or disables the Switch button based on whether a voluntary switch is allowed. */
    public void refresh(boolean canSwitch) {
        this.action2.setDisable(!canSwitch);
    }

    public void setOnAttack(Runnable callback) {
        this.action1.setOnAction(e -> {
            if (callback != null)
                callback.run();
        });
    }

    public void setOnSwitch(Runnable callback) {
        this.action2.setOnAction(e -> {
            if (callback != null)
                callback.run();
        });
    }

    public void setOnSurrender(Runnable callback) {
        this.action4.setOnAction(e -> {
            if (callback != null)
                callback.run();
        });
    }
    public void setOnInventory(Runnable callback) {
        this.action3.setOnAction(e -> {
            if (callback != null)
                callback.run();
        });
    }
}
