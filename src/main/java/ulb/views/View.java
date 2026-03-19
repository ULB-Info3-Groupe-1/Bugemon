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
        // Ensure Modena label lookup can always resolve on this scene tree.
        this.root.setStyle("-fx-text-background-color: -fx-text-inner-color;");
        this.root.prefWidthProperty().bind(this.scene.widthProperty());
        this.root.prefHeightProperty().bind(this.scene.heightProperty());
    }

    /**
     * Reads the current state from the model and updates every UI component.
     *
     * <p>
     * Called by the controller after any model mutation. The view is responsible
     * for pulling all data it needs directly from the model references it holds.
     * The controller never pushes data into the view.
     * </p>
     */
    public abstract void refresh();

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
