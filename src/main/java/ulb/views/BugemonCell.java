package ulb.views;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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

    private static final String EMPTY_NAME = "?";
    private static final Image EMPTY_IMAGE = new Image("/png/unknown.png");

    private final Optional<Bugemon> bugemonData;
    private boolean selected = false;

    public BugemonCell() {
        this.loadFXML();
        this.initializeComponents();

        this.bugemonData = Optional.empty();

        this.update();
    }

    /**
     * Constructor for BugemonCell with a Bugemon.
     * Loads the FXML layout and initializes the view with the bugemon's data.
     *
     * @param bugemon the Bugemon to display (can be null for empty cells)
     */
    public BugemonCell(Bugemon bugemon) {
        this.loadFXML();
        this.initializeComponents();

        this.bugemonData = Optional.of(bugemon);

        this.update();
    }

    private void update() {
        this.updateName();
        this.updateImage();
    }

    private void updateName() {
        this.nameLabel.setText(this.bugemonData.map(Bugemon::getName).orElse(EMPTY_NAME));
    }

    private void updateImage() {
        this.imageView.setImage(
                this.bugemonData.map(d -> new Image(d.getSpriteURL())).orElse(EMPTY_IMAGE));
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
     * Gets the bugemon data associated with this cell.
     * @return the bugemon data
     */
    public Bugemon getBugemonData() {
        return this.bugemonData.orElseThrow(
                ()
                        -> new IllegalStateException(
                                "attempted to get bugemon-data of an empty BugemonCell"));
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
