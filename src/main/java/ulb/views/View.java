package ulb.views;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Base class for all JavaFX views.
 *
 * <p>
 * Each view owns its root {@link Parent} node loaded from FXML. Navigation is performed by swapping the root of the
 * application's single {@link javafx.scene.Scene} via {@link #show(Stage)}, which avoids the resize flash that occurs
 * when replacing the scene itself.
 * </p>
 */
public abstract class View {
    private Parent root;

    /** Called by {@link ViewLoader} after the FXML root has been loaded and injected. */
    public void initRoot(Parent root) {
        this.root = root;
    }

    public abstract String getPath();

    /**
     * Reads the current state from the model and updates every UI component.
     *
     * <p>
     * Called by the controller after any model mutation. The view is responsible for pulling all data it needs directly
     * from the model references it holds. The controller never pushes data into the view.
     * </p>
     */
    public abstract void refresh();

    /** Replaces the scene's root with this view's root, keeping the stage size stable. */
    public void show(Stage stage) {
        stage.getScene().setRoot(this.root);
        stage.show();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
