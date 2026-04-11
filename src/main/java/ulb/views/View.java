package ulb.views;

import java.util.Optional;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Base class for all JavaFX views. Navigation swaps the root of the application's single {@link javafx.scene.Scene} via
 * {@link #show(Stage)}, avoiding the resize flash that occurs when replacing the scene itself.
 */
public abstract class View {
    protected Parent root;

    /** Called by {@link ViewLoader} after the FXML root has been loaded and injected. */
    public void initRoot(Parent newroot) {
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
    protected void showWarningAlert(String title, String message) {
        this.createAlert(title, message, AlertType.WARNING).showAndWait();
    }

    /**
     * Displays a warning dialog with the given title and message. It has two buttons. If the user clicks on the first
     * button, the first button text is returned, otherwise the second button text is returned.
     *
     * @param title
     *            the title of the dialog
     * @param message
     *            the message of the dialog
     * @param button1Text
     *            the text of the first button
     * @param button2Text
     *            the text of the second button
     * @return the text of the clicked button
     */
    protected String showAlertWithTwoButtons(String title, String message, String button1Text, String button2Text) {
        Alert alert = this.createAlert(title, message, AlertType.CONFIRMATION);

        ButtonType button1 = new ButtonType(button1Text);
        ButtonType button2 = new ButtonType(button2Text);

        alert.getButtonTypes().setAll(button1, button2);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == button1 ? button1Text : button2Text;
    }

    private Alert createAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (this.root != null && this.root.getScene() != null) {
            alert.initOwner(this.root.getScene().getWindow());
        }
        return alert;
    }

    protected void showNoActiveTeamAlert(String message) {
        this.showWarningAlert("Aucune équipe active", message);
    }

    public void showAlertChooseTeamToLaunchCombat() {
        this.showNoActiveTeamAlert("Veuillez choisir une equipe pour lancer un combat.");
    }

    protected Parent getRoot() {
        return this.root;
    }
}
