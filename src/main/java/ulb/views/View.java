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
    protected void initRoot(Parent newroot) {
        this.root = newroot;
    }

    /** Returns the FXML resource path used by {@link ViewLoader} to load this view. */
    protected abstract String getPath();

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
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (this.root != null && this.root.getScene() != null) {
            alert.initOwner(this.root.getScene().getWindow());
        }

        alert.showAndWait();
    }

    public void showNoTeamAlert() {
        this.showAlert("Aucune équipe active", "Veuillez créer ou charger une équipe avant de lancer un combat.");
    }

    /**
     * Lock the player's input.
     * @param locked (boolean) true to lock, false to unlock
     */
    protected void setInputLocked(boolean locked) {
        this.root.setMouseTransparent(locked);
        if (this.root.getScene() != null) {
            if (locked) {
                this.root.getScene().setCursor(javafx.scene.Cursor.WAIT);
            } else {
                this.root.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
            }
        }
    }

}
