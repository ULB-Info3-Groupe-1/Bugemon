package ulb.views;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import ulb.models.bugemon.Bugemon;

/**
 * Reusable custom component representing a single Bugemon cell
 * with an image and name label.
 */
public class BugemonCell extends VBox {
    @FXML private StackPane imagePane;
    @FXML private ImageView imageView;
    @FXML private Label nameLabel;

    private static final String FXML_PATH = "/fxml/BugemonCell.fxml";
    private static final double IMAGE_SIZE = 96;

    private Bugemon bugemonData;
    private boolean selected = false;

    /**
     * Constructor for BugemonCell with a Bugemon.
     * Loads the FXML layout and initializes the view with the bugemon's data.
     *
     * @param bugemon the Bugemon to display (can be null for empty cells)
     */
    public BugemonCell(Bugemon bugemon) {
        this.loadFXML();
        this.initializeComponents();

        this.setImage(bugemon.getSpriteURL());
        this.setName(bugemon.getName());

        this.setBugemonData(bugemon);
    }

    /**
     * Private helper to load the FXML layout.
     */
    private void loadFXML() {
		URL url = getClass().getResource(FXML_PATH);
        FXMLLoader loader = new FXMLLoader(url);

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load BugemonCell.fxml", e);
        }
    }

    /**
     * Private helper to initialize component styling and properties.
     */
    private void initializeComponents() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(2);
        this.getStyleClass().add("bugemon-cell");

        this.imageView.setFitWidth(IMAGE_SIZE);
        this.imageView.setFitHeight(IMAGE_SIZE);
        this.imageView.setPreserveRatio(true);

        this.imagePane.setMinSize(IMAGE_SIZE, IMAGE_SIZE);
        this.imagePane.setMaxSize(IMAGE_SIZE, IMAGE_SIZE);

        this.nameLabel.getStyleClass().add("bugemon-cell-name");
    }

    /**
     * Sets the image to display in this cell using a URL.
     * @param imageUrl the URL of the image to display
     */
    private void setImage(String imageUrl) {
        this.imageView.setImage(new Image(imageUrl));
    }

    /**
     * Sets the name to display below the image.
     * @param name the name to display
     */
    private void setName(String name) {
        this.nameLabel.setText(name);
    }

    /**
     * Sets the bugemon data associated with this cell.
     * This data can be retrieved later via getUserData().
     * @param bugemonData the bugemon data to associate with this cell
     */
    private void setBugemonData(Bugemon bugemonData) {
        this.bugemonData = bugemonData;
    }

    /**
     * Gets the bugemon data associated with this cell.
     * @return the bugemon data
     */
    public Bugemon getBugemonData() {
        return this.bugemonData;
    }

    /**
     * Marks this cell as selected, applying the selected styling.
     */
    private void select() {
        if (!this.selected) {
            this.selected = true;
            this.imageView.getStyleClass().add("bugemon-image-selected");
            this.getStyleClass().remove("bugemon-cell");
            this.getStyleClass().add("bugemon-cell-selected");
        }
    }

    /**
     * Marks this cell as unselected, removing the selected styling.
     */
    private void unselect() {
        if (this.selected) {
            this.selected = false;
            this.imageView.getStyleClass().remove("bugemon-image-selected");
            this.getStyleClass().remove("bugemon-cell-selected");
            this.getStyleClass().add("bugemon-cell");
        }
    }

    /**
     * Sets the selection state of this cell.
     * @param selected true to select, false to unselect
     */
    public void setSelected(boolean selected) {
        if (selected) {
            this.select();
        } else {
            this.unselect();
        }
    }
}
