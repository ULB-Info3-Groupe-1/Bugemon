package bugemon.client.views.combat.components;

import java.io.File;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import bugemon.common.Configuration;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.client.views.components.ComponentView;

/**
 * Action menu listing the Bugemons available for the player to switch into. Each entry is rendered as a row containing
 * the Bugemon's sprite thumbnail and a button showing its name, level, and current HP.
 *
 * <p>
 * When the switch is not forced, a back button is appended at the bottom so the player can return to the main action
 * menu without switching. All selections are forwarded through {@link Listener}.
 */
public class SwitchMenuView extends ComponentView {
    private static final int SPRITE_SIZE = 40;

    private Listener listener;

    public SwitchMenuView() {
        super(Configuration.Paths.Fxml.COMPONENT_SWITCH_MENU);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Clears and repopulates the menu with the given Bugemons available for switching.
     *
     * @param available
     *            Bugemons that can be switched in (typically alive, non-active team members)
     * @param forced
     *            {@code true} if this switch cannot be cancelled; hides the back button when set
     */
    public void show(List<CombatBugemon> available, boolean forced) {
        this.getChildren().clear();

        for (CombatBugemon b : available) {
            this.getChildren().add(this.createSwitchRow(b));
        }

        if (!forced) {
            Button back = new Button("Retour");
            back.getStyleClass().addAll("btn", "btn-secondary", "menu-btn-min");
            back.setMaxWidth(Double.MAX_VALUE);
            back.setWrapText(true);
            back.setOnAction(e -> this.listener.onBack());
            this.getChildren().add(back);
        }
    }

    private HBox createSwitchRow(CombatBugemon b) {
        HBox row = new HBox();
        row.getStyleClass().add("switch-row");

        File file = new File(Configuration.Paths.SPRITES + b.getSpritePath());
        ImageView sprite = new ImageView(new Image(file.toURI().toString(), SPRITE_SIZE, SPRITE_SIZE, true, false));
        sprite.setFitWidth(SPRITE_SIZE);
        sprite.setFitHeight(SPRITE_SIZE);
        sprite.setPreserveRatio(true);

        Button btn = new Button(
                b.getName() + " Nv." + b.getLevel() + "  " + b.getCurrentHp() + "/" + b.getMaxHp() + " PV");
        btn.getStyleClass().addAll("btn", "btn-action-blue");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setWrapText(true);
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setOnAction(e -> this.listener.onSwitch(b));

        row.getChildren().addAll(sprite, btn);
        return row;
    }

    /** Callback interface for switch menu interactions. */
    public interface Listener {

        /**
         * Called when the player selects a Bugemon to switch in.
         *
         * @param bugemon
         *            the {@link CombatBugemon} chosen for the switch
         */
        void onSwitch(CombatBugemon bugemon);

        /** Called when the player clicks the back button (voluntary switch only). */
        void onBack();

    }
}
