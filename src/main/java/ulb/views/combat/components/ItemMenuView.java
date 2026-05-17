package ulb.views.combat.components;

import java.util.Map;
import javafx.scene.control.Button;

import ulb.Configuration;
import ulb.models.item.Item;
import ulb.views.components.ComponentView;

/** Reusable component displaying the player's inventory as a list of clickable item buttons. */
public class ItemMenuView extends ComponentView {

    private Listener listener;

    public ItemMenuView() {
        super(Configuration.Paths.Fxml.COMPONENT_ITEM_MENU);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Clears and repopulates the menu with the given inventory entries. */
    public void show(Map<Item, Integer> inventory) {
        this.getChildren().clear();

        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            this.getChildren().add(this.createItemButton(entry.getKey(), entry.getValue()));
        }

        Button back = new Button("Retour");
        back.getStyleClass().addAll("btn", "btn-secondary", "menu-btn-min");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setWrapText(true);
        back.setOnAction(e -> this.listener.onBack());
        this.getChildren().add(back);
    }

    private Button createItemButton(Item item, int quantity) {
        Button btn = new Button(item.name() + " ×" + quantity);
        btn.getStyleClass().addAll("btn", "btn-warning", "menu-btn-min");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setWrapText(true);
        btn.setOnAction(e -> this.listener.onItemSelected(item));
        btn.setOnMouseEntered(e -> this.listener.onItemHovered(item));
        btn.setOnMouseExited(e -> this.listener.onItemLeft());
        return btn;
    }

    public interface Listener {

        void onItemSelected(Item item);

        void onItemHovered(Item item);

        void onItemLeft();

        void onBack();

    }
}
