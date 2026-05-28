package bugemon.client.views.components;

import java.io.IOException;
import java.io.UncheckedIOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

/**
 * Base class for custom JavaFX components that load their layout from an FXML resource via {@code fx:root}.
 */
public abstract class ComponentView extends VBox {
    protected ComponentView(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(ComponentView.class.getResource(fxmlPath));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load component: " + fxmlPath, e);
        }
    }
}
