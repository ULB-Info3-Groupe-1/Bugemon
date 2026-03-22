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

/**
 * Reusable custom component representing a single Bugemon cell
 * with an image and name label.
 */
public class BugemonCell extends VBox {
    @FXML private StackPane imagePane;
    @FXML private ImageView imageView;
    @FXML private Label nameLabel;

    private static final double IMAGE_SIZE = 96;

    private Object bugemonData;
    private boolean selected = false;

    /**
     * Constructor for BugemonCell. Loads the FXML layout and initializes the view.
     */
    public BugemonCell() {
        URL url = getClass().getResource("/fxml/BugemonCell.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load BugemonCell.fxml", e);
        }

        // Apply default styling
        this.setAlignment(Pos.CENTER);
        this.setSpacing(2);
        this.getStyleClass().add("bugemon-cell");

        // Configure image view
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);

        // Configure image pane
        imagePane.setMinSize(IMAGE_SIZE, IMAGE_SIZE);
        imagePane.setMaxSize(IMAGE_SIZE, IMAGE_SIZE);

        // Configure name label
        nameLabel.getStyleClass().add("bugemon-cell-name");
    }

    /**
     * Sets the image to display in this cell.
     * @param image the Image to display
     */
    public void setImage(Image image) {
        imageView.setImage(image);
    }

    /**
     * Sets the image to display in this cell using a URL.
     * @param imageUrl the URL of the image to display
     */
    public void setImage(String imageUrl) {
        imageView.setImage(new Image(imageUrl));
    }

    /**
     * Sets the name to display below the image.
     * @param name the name to display
     */
    public void setName(String name) {
        nameLabel.setText(name);
    }

    /**
     * Sets the bugemon data associated with this cell.
     * This data can be retrieved later via getUserData().
     * @param data the bugemon data to associate with this cell
     */
    public void setBugemonData(Object data) {
        this.bugemonData = data;
        this.setUserData(data);
    }

    /**
     * Gets the bugemon data associated with this cell.
     * @return the bugemon data
     */
    public Object getBugemonData() {
        return bugemonData;
    }

    /**
     * Marks this cell as selected, applying the selected styling.
     */
    public void select() {
        if (!selected) {
            selected = true;
            imageView.getStyleClass().add("bugemon-image-selected");
            this.getStyleClass().remove("bugemon-cell");
            this.getStyleClass().add("bugemon-cell-selected");
        }
    }

    /**
     * Marks this cell as unselected, removing the selected styling.
     */
    public void unselect() {
        if (selected) {
            selected = false;
            imageView.getStyleClass().remove("bugemon-image-selected");
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
            select();
        } else {
            unselect();
        }
    }

    /**
     * Returns whether this cell is currently selected.
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }
}
