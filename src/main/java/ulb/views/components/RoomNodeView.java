package ulb.views.components;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import ulb.controllers.combat.TowerController.RoomState;
import ulb.models.tower.room.RoomType;

/**
 * Reusable component representing a single room node in the floor map. Configurable to represent different room types
 */
public class RoomNodeView extends StackPane {
    private static final String FXML_PATH = "/fxml/components/RoomNode.fxml";
    private static final String ROOM_BASE_PATH = "/png/rooms/";

    @FXML
    private ImageView roomToken;

    @FXML
    private ImageView roomTypeIcon;

    @FXML
    private StackPane stateOverlay;

    @FXML
    private Label stateLabel;

    private int row;
    private int col;
    private Listener listener;

    public RoomNodeView() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(FXML_PATH));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load component: " + FXML_PATH, e);
        }
    }

    @FXML
    private void initialize() {
        this.roomToken.setImage(this.loadRequiredImage(ROOM_BASE_PATH + "base.png"));
    }

    /**
     * Set the type of room this node represents (e.g., "START", "COMBAT", "BOSS", "REWARD", "EMPTY"). Updates the visual
     *
     * @param roomType
     *            The type of room (e.g., "START", "COMBAT", "BOSS", "REWARD", "EMPTY")
     */
    public void setRoomType(RoomType roomType) {
        this.getStyleClass().removeAll("room-start", "room-combat", "room-boss", "room-reward", "room-empty");

        String normalizedRoomType = roomType.toString().toLowerCase();

        String styleClass = "room-" + normalizedRoomType;
        this.getStyleClass().add(styleClass);

        String iconPath = ROOM_BASE_PATH + normalizedRoomType + ".png";
        this.roomTypeIcon.setImage(this.loadOptionalImage(iconPath));
    }

    /**
     * Configure the visual state of the room (CURRENT, AVAILABLE, VISITED, LOCKED). Applies the appropriate CSS styles
     * and overlays.
     *
     * @param roomState
     *            The state of the room (e.g., "CURRENT", "AVAILABLE", "VISITED", "LOCKED")
     */
    public void setRoomState(RoomState roomState) {
        this.getStyleClass().removeAll("current", "available", "visited", "locked");

        this.getStyleClass().add(roomState.toString().toLowerCase());

        // Configurer l'overlay selon l'état
        switch (roomState) {
            case CURRENT -> {
                this.stateLabel.setText("");
                this.stateOverlay.setVisible(true);
                break;
            }
            case VISITED -> {
                this.stateLabel.setText("✓");
                this.stateOverlay.setVisible(true);
                break;
            }
            case AVAILABLE -> {
                this.stateLabel.setText("");
                this.stateOverlay.setVisible(true);
                break;
            }
            case LOCKED -> {
                this.stateLabel.setText("");
                this.stateOverlay.setVisible(false);
                break;
            }
            default -> {
                this.stateLabel.setText("");
                this.stateOverlay.setVisible(false);
                break;
            }
        }

        this.setDisable(roomState == RoomState.LOCKED);
    }

    /**
     * Registers a listener to receive callbacks for room interactions.
     *
     * @param listener
     *            The listener to be notified of room events
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        if (this.listener != null && !this.isDisabled()) {
            this.listener.onRoomClicked(this.row, this.col);
        }
    }

    /**
     * Sets the grid position of this room node. This is used for event callbacks to identify which room was interacted
     * with.
     *
     * @param rowPosition
     *            Case line
     * @param colPosition
     *            Case column
     */
    public void setPosition(int rowPosition, int colPosition) {
        this.row = rowPosition;
        this.col = colPosition;
        this.setUserData(new RoomPosition(rowPosition, colPosition));
    }

    /**
     * Retrieves the row of this room node.
     *
     * @return The row of the room node
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Retrieves the column of this room node.
     *
     * @return The column of the room node
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Record for storing the position of a room.
     */
    public record RoomPosition(int row, int col) {
    }

    /**
     * Callback interface for room node interactions. Dispatches room events to the controller exclusively through this
     * interface.
     */
    public interface Listener {
        /**
         * Called when a room node is clicked by the player.
         *
         * @param row
         *            The row position of the clicked room
         * @param col
         *            The column position of the clicked room
         */
        void onRoomClicked(int row, int col);
    }

    private Image loadRequiredImage(String imagePath) {
        URL imageUrl = this.getClass().getResource(imagePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing required image resource: " + imagePath);
        }
        return new Image(imageUrl.toExternalForm());
    }

    private Image loadOptionalImage(String imagePath) {
        URL imageUrl = this.getClass().getResource(imagePath);
        if (imageUrl == null) {
            return null;
        }
        return new Image(imageUrl.toExternalForm());
    }
}
