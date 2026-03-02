package ulb.views;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * View
 *
 * Base class for all JavaFX views.
 * Loads an FXML layout and manages its associated scene.
 */
public abstract class View {

    protected final Pane root;
    protected final Scene scene;

    /**
     * Loads the FXML file and initializes the scene.
     *
     * @param fxmlPath path to the FXML resource
     * @throws IOException if the FXML file cannot be loaded
     */
    public View(String fxmlPath) throws IOException {
        URL url = View.class.getResource(fxmlPath);
        FXMLLoader loader = new FXMLLoader(url);

        // TODO: understand what this means (comes from the TP):
        // Casting `this` to get rid of the warning "Leaking `this` in constructor"
        loader.setController((Object) this);

        this.root = loader.load();
        this.scene = new Scene(root);
        this.root.prefWidthProperty().bind(this.scene.widthProperty());
        this.root.prefHeightProperty().bind(this.scene.heightProperty());

    }

    /**
     * Displays this view on the given stage.
     *
     * @param stage JavaFX stage where the view is shown
     */
    public void show(Stage stage) {
        stage.setScene(this.scene);
        stage.show();
    }

}
