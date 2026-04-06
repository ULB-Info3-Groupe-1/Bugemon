package ulb.views;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Base class for all JavaFX views. Navigation swaps the root of the application's single {@link javafx.scene.Scene} via
 * {@link #show(Stage)}, avoiding the resize flash that occurs when replacing the scene itself.
 */
public abstract class View {
    private Parent root;

    /** Called by {@link ViewLoader} after the FXML root has been loaded and injected. */
    public void initRoot(Parent newroot) {
        this.root = newroot;
    }

    /** Returns the FXML resource path used by {@link ViewLoader} to load this view. */
    public abstract String getPath();

    /**
     * Reads the current state from the model and updates every UI component. Called by the controller after any model
     * mutation; the controller never pushes data into the view.
     */
    public abstract void refresh();

    /** Replaces the scene's root with this view's root, keeping the stage size stable. */
    public void show(Stage stage) {
        stage.getScene().setRoot(this.root);
        stage.show();
    }

    /** Displays a warning dialog with the given title and message. */
    public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (this.root != null && this.root.getScene() != null) {
            alert.initOwner(this.root.getScene().getWindow());
        }

        alert.showAndWait();
    }
}
