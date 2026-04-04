package ulb.views.combat.components;

import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.control.Button;

import ulb.models.bugemon.Item;
import ulb.views.components.ComponentView;

/** Reusable component displaying the player's inventory as a list of clickable item buttons. */
public class ItemMenuView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/ItemMenu.fxml";
    private static final double MIN_BUTTON_WIDTH = 200;

    private Consumer<Item> onItemSelected;
    private Runnable onBack;

    public ItemMenuView() {
        super(FXML_PATH);
    }

    /** Sets the callback invoked when the user clicks an item button. */
    public void setOnItemSelected(Consumer<Item> callback) {
        this.onItemSelected = callback;
    }

    /** Sets the callback invoked when the user clicks the back button. */
    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }

    /** Clears and repopulates the menu with the given inventory entries. */
    public void show(Map<Item, Integer> inventory) {
        this.getChildren().clear();

        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            this.getChildren().add(this.createItemButton(entry.getKey(), entry.getValue()));
        }

        Button back = new Button("Retour");
        back.getStyleClass().add("action-button");
        back.setMinWidth(MIN_BUTTON_WIDTH);
        back.setOnAction(e -> {
            if (this.onBack != null) {
                this.onBack.run();
            }
        });
        this.getChildren().add(back);
    }

    private Button createItemButton(Item item, int quantity) {
        Button btn = new Button(item.name() + " ×" + quantity);
        btn.getStyleClass().add("switch-menu-button");
        btn.setMinWidth(MIN_BUTTON_WIDTH);
        btn.setOnAction(e -> {
            if (this.onItemSelected != null) {
                this.onItemSelected.accept(item);
            }
        });
        return btn;
    }
}
