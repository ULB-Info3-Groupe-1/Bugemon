package ulb.views.components;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import ulb.models.bugemon.Bugemon;

/** Reusable custom component representing a single Bugemon cell with an image and name label. */
public class BugemonCardView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/BugemonCard.fxml";
    private static final String EMPTY_NAME = "?";
    private static final Image EMPTY_IMAGE = new Image("/png/unknown.png");

    @FXML
    private StackPane imagePane;
    @FXML
    private ImageView imageView;
    @FXML
    private Label nameLabel;

    private final Optional<Bugemon> bugemonData;
    private boolean selected = false;
    private Consumer<Bugemon> onClickCallback;

    /** Constructs an empty placeholder card with a default image and {@code "?"} as name. */
    public BugemonCardView() {
        this(Optional.empty());
    }

    public BugemonCardView(Bugemon bugemon) {
        this(Optional.of(bugemon));
    }

    private BugemonCardView(Optional<Bugemon> bugemonData) {
        super(FXML_PATH);
        this.bugemonData = bugemonData;
        this.nameLabel.setText(bugemonData.map(Bugemon::getName).orElse(EMPTY_NAME));
        this.imageView.setImage(
                bugemonData.map(d -> new Image(new File("resources/sprites/" + d.getSpriteURL()).toURI().toString()))
                        .orElse(EMPTY_IMAGE));
    }

    @FXML
    private void onSelected() {
        this.bugemonData.ifPresent(b -> {
            if (this.onClickCallback != null) {
                this.onClickCallback.accept(b);
            }
        });
    }

    /**
     * Registers the callback invoked when this card is clicked. Does not fire if the card is empty (no Bugemon data).
     */
    public void setOnClick(Consumer<Bugemon> callback) {
        this.onClickCallback = callback;
    }

    /** Applies or removes the selected visual style. */
    public void setSelected(boolean selected) {
        if (selected) {
            this.select();
        } else {
            this.unselect();
        }
    }

    private void select() {
        if (!this.selected) {
            this.selected = true;
            this.imageView.getStyleClass().add("bugemon-image-selected");
            this.getStyleClass().remove("bugemon-cell");
            this.getStyleClass().add("bugemon-cell-selected");
        }
    }

    private void unselect() {
        if (this.selected) {
            this.selected = false;
            this.imageView.getStyleClass().remove("bugemon-image-selected");
            this.getStyleClass().remove("bugemon-cell-selected");
            this.getStyleClass().add("bugemon-cell");
        }
    }
}
