package ulb.views;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        loader.setController((Object)this);

        this.root = loader.load();
        this.scene = new Scene(root);
        this.root.prefWidthProperty().bind(this.scene.widthProperty());
        this.root.prefHeightProperty().bind(this.scene.heightProperty());
    }

    /**
     * Returns the root pane of this view.
     */
    public Pane getRoot() {
        return this.root;
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

    /**
     * Displays an alert dialog with the specified title and message.
     * @param title the title of the alert dialog
     * @param message the content message of the alert dialog
     */
    public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
