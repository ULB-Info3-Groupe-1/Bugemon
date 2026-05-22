package ulb.views.combat.components;

import java.io.File;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.CombatBugemon;
import ulb.views.components.ComponentView;

/**
 * Action menu listing the Bugemons available for the player to switch into. Dispatches switch selections through the
 * callback registered via {@link Listener#onSwitch(Bugemon)}. When not a forced switch, a back button is shown and
 * dispatches through the callback registered via {@link Listener#onBack()}.
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

    public interface Listener {

        void onSwitch(CombatBugemon bugemon);

        void onBack();

    }
}
