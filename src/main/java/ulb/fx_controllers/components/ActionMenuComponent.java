package ulb.fx_controllers.components;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public abstract class ActionMenuComponent extends VBox {
    private final static String FXML_PATH = "/fxml/ActionMenu.fxml";

    @FXML
    protected VBox actionContainer;

    public ActionMenuComponent() {
        URL url = getClass().getResource(FXML_PATH);
        FXMLLoader loader = new FXMLLoader(url);

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load ActionMenu.fxml", e);
        }
    }

    public void setActions(List<String> actionLabels) {
        actionContainer.getChildren().clear();

        HBox row1 = new HBox(10); // 10 is the spacing between buttons
        HBox row2 = new HBox(10);

        for (int i = 0; i < actionLabels.size(); i++) {
            Button button = new Button(actionLabels.get(i));
            button.setPrefWidth(250.0);
            button.setPrefHeight(100.0);
            button.getStyleClass().add("action-button");

            if (i < 2) {
                row1.getChildren().add(button);
            } else {
                row2.getChildren().add(button);
            }
        }

        actionContainer.getChildren().addAll(row1, row2);
    }
}
