package ulb.views.combat.components;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import ulb.models.bugemon.Bugemon;
import ulb.views.components.ComponentView;

/**
 * Action menu listing the Bugemons available for the player to switch into.
 *
 * <p>
 * Dispatches switch selections through the callback registered via {@link #setOnSwitch(Consumer)}. When not a forced
 * switch, a back button is shown and dispatches through the callback registered via {@link #setOnBack(Runnable)}.
 * </p>
 */
public class SwitchMenuView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/SwitchMenu.fxml";
    /** Sprite dimensions — not CSS-styleable on ImageView in JavaFX. */
    private static final int SPRITE_SIZE = 40;

    private Consumer<Bugemon> onSwitch;
    private Runnable onBack;

    public SwitchMenuView() {
        super(FXML_PATH);
    }

    public void setOnSwitch(Consumer<Bugemon> callback) {
        this.onSwitch = callback;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }

    /** Clears and repopulates the menu with the available Bugemons. */
    public void show(List<Bugemon> available, boolean forced) {
        this.getChildren().clear();

        for (Bugemon b : available) {
            this.getChildren().add(this.createSwitchRow(b));
        }

        if (!forced) {
            Button back = new Button("Retour");
            back.getStyleClass().addAll("btn", "btn-secondary", "menu-btn-min");
            back.setOnAction(e -> {
                if (this.onBack != null) {
                    this.onBack.run();
                }
            });
            this.getChildren().add(back);
        }
    }

    private HBox createSwitchRow(Bugemon b) {
        HBox row = new HBox();
        row.getStyleClass().add("switch-row");

        File file = new File("resources/sprites/" + b.getSpriteURL());
        ImageView sprite = new ImageView(new Image(file.toURI().toString(), SPRITE_SIZE, SPRITE_SIZE, true, false));
        sprite.setFitWidth(SPRITE_SIZE);
        sprite.setFitHeight(SPRITE_SIZE);
        sprite.setPreserveRatio(true);

        Button btn = new Button(b.getName() + " Nv." + b.getLevel() + "  " + b.getHp() + "/" + b.getMaxHp() + " PV");
        btn.getStyleClass().addAll("btn", "btn-action-blue");
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setOnAction(e -> {
            if (this.onSwitch != null) {
                this.onSwitch.accept(b);
            }
        });

        row.getChildren().addAll(sprite, btn);
        return row;
    }
}
