package ulb.views.components;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import ulb.Configuration;
import ulb.common.dto.display.BugemonDisplayDTO;

/**
 * Reusable custom component representing a single Bugemon cell with an image and name label.
 */
public class BugemonCardView extends ComponentView {
    private static final String EMPTY_NAME = "?";
    private static final Image EMPTY_IMAGE = new Image(Configuration.Paths.DEFAULT_SPRITE);

    @FXML
    private ImageView imageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label levelLabel;

    private Listener listener;

    private final BugemonDisplayDTO bugemon;
    private final BugemonDetailPopupView detailPopup;

    /** Constructs an empty placeholder card with a default sprite and a {@code "?"} name. */
    public BugemonCardView() {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_CARD);
        this.bugemon = null;
        this.detailPopup = null;

        this.nameLabel.setText(EMPTY_NAME);
        this.imageView.setImage(EMPTY_IMAGE);
    }

    /**
     * Constructs a card populated with the given Bugemon's sprite, name, and level; also registers a right-click
     * context menu that opens a {@link BugemonDetailPopupView}.
     *
     * @param bugemon
     *            the Bugemon data to display
     */
    public BugemonCardView(BugemonDisplayDTO bugemon) {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_CARD);
        this.bugemon = bugemon;
        this.detailPopup = new BugemonDetailPopupView(bugemon);

        this.nameLabel.setText(bugemon.getName());
        this.levelLabel.setText(String.valueOf(bugemon.level()));
        File spriteFile = new File(Configuration.Paths.SPRITES + bugemon.getSpritePath());
        this.imageView.setImage(new Image(spriteFile.toURI().toString()));
        this.setOnContextMenuRequested(this.detailPopup::show);
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

        if (this.listener != null && this.bugemon != null) {
            this.listener.onBugemonSelected(this.bugemon);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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

    public void setName(String name) {
        this.nameLabel.setText(name.isEmpty() ? EMPTY_NAME : name);
    }

    public void setSprite(File file) {
        this.imageView.setImage(new Image(file.toURI().toString()));
    }

    public void removeSprite() {
        this.imageView.setImage(EMPTY_IMAGE);
    }

    /** Callback interface for Bugemon card click interactions. */
    public interface Listener {
        /** Called when the player left-clicks this card. */
        void onBugemonSelected(BugemonDisplayDTO bugemon);
    }
}
