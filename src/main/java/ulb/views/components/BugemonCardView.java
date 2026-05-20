package ulb.views.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import ulb.Configuration;
import ulb.common.dto.BugemonDisplayDTO;

/** Reusable custom component representing a single Bugemon cell with an image and name label. */
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

    public BugemonCardView() {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_CARD);
        this.bugemon = null;
        this.detailPopup = null;

        this.nameLabel.setText(EMPTY_NAME);
        this.imageView.setImage(EMPTY_IMAGE);
    }

    public BugemonCardView(BugemonDisplayDTO bugemon) {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_CARD);
        this.bugemon = bugemon;
        this.detailPopup = new BugemonDetailPopupView(bugemon);

        this.nameLabel.setText(bugemon.getName());
        this.levelLabel.setText(String.valueOf(bugemon.level()));
        this.imageView.setImage(new Image(bugemon.getSpritePath()));
        this.setOnContextMenuRequested(e -> this.detailPopup.show(e));
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
            this.listener.onClick(this.bugemon);
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

    public interface Listener {
        void onClick(BugemonDisplayDTO bugemon);
    }
}
