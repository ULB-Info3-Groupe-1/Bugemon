package ulb.views.combat.components;

import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.control.Button;

import ulb.models.bugemon.Item;
import ulb.views.components.ComponentView;

/** Reusable component displaying the player's inventory as a list of clickable item buttons. */
public class ItemMenuView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/ItemMenu.fxml";

    private Consumer<Item> onItemSelected;
    private Consumer<Item> onItemHovered;
    private Runnable onItemLeft;
    private Runnable onBack;

    public ItemMenuView() {
        super(FXML_PATH);
    }

    public void setOnItemSelected(Consumer<Item> callback) {
        this.onItemSelected = callback;
    }

    public void setOnItemHovered(Consumer<Item> callback) {
        this.onItemHovered = callback;
    }

    public void setOnItemLeft(Runnable callback) {
        this.onItemLeft = callback;
    }

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
        back.getStyleClass().addAll("btn", "btn-secondary", "menu-btn-min");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setWrapText(true);
        back.setOnAction(e -> {
            if (this.onBack != null) {
                this.onBack.run();
            }
        });
        this.getChildren().add(back);
    }

    private Button createItemButton(Item item, int quantity) {
        Button btn = new Button(item.name() + " ×" + quantity);
        btn.getStyleClass().addAll("btn", "btn-warning", "menu-btn-min");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setWrapText(true);
        btn.setOnAction(e -> {
            if (this.onItemSelected != null) {
                this.onItemSelected.accept(item);
            }
        });
        btn.setOnMouseEntered(e -> {
            if (this.onItemHovered != null) {
                this.onItemHovered.accept(item);
            }
        });
        btn.setOnMouseExited(e -> {
            if (this.onItemLeft != null) {
                this.onItemLeft.run();
            }
        });
        return btn;
    }
}
