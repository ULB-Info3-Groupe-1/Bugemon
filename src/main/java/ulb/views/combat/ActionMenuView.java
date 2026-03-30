package ulb.views.combat;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ActionMenuView extends VBox {
    private final static String FXML_PATH = "/fxml/ActionMenu.fxml";

    @FXML protected Button action1;
    @FXML protected Button action2;
    @FXML protected Button action3;
    @FXML protected Button action4;

    public ActionMenuView() {
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
}
