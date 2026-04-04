package ulb.views;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/** Base class for all JavaFX views. Loads an FXML layout and manages its associated scene. */
public abstract class View {
    protected Scene scene;

    public void initScene(Parent root) {
        this.scene = new Scene(root);
        this.scene.getStylesheets().add(View.class.getResource("/css/theme.css").toExternalForm());
        Region region = (Region) root;
        region.prefWidthProperty().bind(this.scene.widthProperty());
        region.prefHeightProperty().bind(this.scene.heightProperty());
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

    public void show(Stage stage) {
        stage.setScene(this.scene);
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
