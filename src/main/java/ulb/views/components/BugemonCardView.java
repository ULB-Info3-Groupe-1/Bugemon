package ulb.views.components;

import java.io.File;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;

/**
 * Reusable custom component representing a single Bugemon cell with an image and name label.
 */
public class BugemonCardView extends ComponentView {
    private static final String EMPTY_NAME = "?";
    private static final Image EMPTY_IMAGE = new Image(Configuration.Paths.DEFAULT_SPRITE);

    @FXML
    private StackPane imagePane;
    @FXML
    private ImageView imageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label levelLabel;

    private Listener listener;

    private final Optional<Bugemon> bugemonData;

    /**
     * Constructs an empty placeholder card with a default image and {@code "?"} as name.
     */
    public BugemonCardView() {
        this(Optional.empty());
    }

    public BugemonCardView(Bugemon bugemon) {
        this(Optional.of(bugemon));
    }

    private BugemonCardView(Optional<Bugemon> bugemonData) {
        super(Configuration.Paths.FXML.COMPONENT_BUGEMON_CARD);
        this.bugemonData = bugemonData;
        this.nameLabel.setText(bugemonData.map(Bugemon::getName).orElse(EMPTY_NAME));
        this.levelLabel.setText(bugemonData.map(b -> "Lv." + b.getLevel()).orElse(""));
        this.imageView.setImage(bugemonData
                .map(d -> new Image(new File(Configuration.Paths.SPRITES + d.getSpriteURL()).toURI().toString()))
                .orElse(EMPTY_IMAGE));
        bugemonData.ifPresent(b -> this.setOnContextMenuRequested(e -> BugemonDetailPopupView.show(b, e)));
    }

    public void hideLevelLabel() {
        this.levelLabel.setVisible(false);
        this.levelLabel.setManaged(false);
    }

    @FXML
    private void onClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        this.bugemonData.ifPresent(b -> this.listener.onClick(b));
    }

    public void setSprite(File file) {
        Image image = new Image(file.toURI().toString());
        this.imageView.setImage(image);
    }

    public void removeSprite() {
        this.imageView.setImage(EMPTY_IMAGE);
    }

    public void setName(String name) {
        this.nameLabel.setText(name);
    }

    public void select() {
        this.imageView.getStyleClass().add("bugemon-image-selected");
        this.getStyleClass().remove("bugemon-cell");
        this.getStyleClass().add("bugemon-cell-selected");
    }
    
    public void unselect() {
        this.imageView.getStyleClass().remove("bugemon-image-selected");
        this.getStyleClass().remove("bugemon-cell-selected");
        this.getStyleClass().add("bugemon-cell");
    }

    public interface Listener {

        void onClick(Bugemon bugemon);

    }
}
