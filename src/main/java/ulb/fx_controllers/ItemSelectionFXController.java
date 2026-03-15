package ulb.fx_controllers;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import ulb.common.dto.ItemDTO;
import ulb.controllers.combat.ManualCombatController;

public class ItemSelectionFXController extends VBox {

    @FXML
    private TilePane itemsList;

    private final ManualCombatController controller;

    public ItemSelectionFXController(ManualCombatController controller) {
        this.controller = controller;
    }

    @FXML
    public void initialize() {
    }

    public void setItemsTiles(List<ItemDTO> items) {
        itemsList.getChildren().clear();
        for (ItemDTO item : items) {
            addItemTile(item);
        }
    }

    private void addItemTile(ItemDTO item) {
        Button itemButton = new Button(item.getName());
        itemButton.setPrefWidth(100);
        itemButton.setPrefHeight(50);
        itemButton.setOnAction(this::handleItemClick);
        itemButton.setUserData(item); // Store the ItemDTO in the button
        itemsList.getChildren().add(itemButton);
    }

    @FXML
    private void handleItemClick(ActionEvent event) {
        Button clickedItem = (Button) event.getSource();
        ItemDTO selectedItem = (ItemDTO) clickedItem.getUserData();

        if (controller != null) {
            controller.onItemSelected(selectedItem.getId());
        }
    }
}
