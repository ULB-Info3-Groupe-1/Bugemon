package ulb.fx_controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ulb.controllers.MetaController;

public abstract class FXController {

	protected MetaController metaController;
	protected Pane root;
	protected Scene scene;

	public void setMetaController(MetaController metaController) {
	this.metaController = metaController;
	}

	@FXML
	protected void initialize() {
	    // To implement if needed in subclasses
	}

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

	// Called after the loading of the fxml to create the scene
	public void initScene(Pane root) {
	    this.root = root;
		this.scene = new Scene(root);
		this.root.prefWidthProperty().bind(scene.widthProperty());
		this.root.prefHeightProperty().bind(scene.heightProperty());
	}
}
