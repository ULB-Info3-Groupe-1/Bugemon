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

import ulb.models.tower.room.Room;
import ulb.models.tower.room.Room.RoomPosition;
import ulb.models.tower.room.Room.RoomState;

/**
 * Reusable component representing a single room node in the floor map.
 * Configurable to represent different room types
 */
public class RoomView extends StackPane {
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

    private final Room room;
    private Listener listener;

    public RoomView(Room room) {
        this.room = room;
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
        this.roomToken.setImage(this.loadImage(ROOM_BASE_PATH + "base.png"));
        this.initRoom();
    }

    public void initRoom() {
        this.getStyleClass().removeAll("room-start", "room-combat", "room-boss", "room-reward", "room-empty");

        String normalizedRoomType = this.room.getType().toString().toLowerCase();

        String styleClass = "room-" + normalizedRoomType;
        this.getStyleClass().add(styleClass);

        String iconPath = ROOM_BASE_PATH + normalizedRoomType + ".png";
        this.roomTypeIcon.setImage(this.loadImage(iconPath));
    }

    /**
     * Configure the visual state of the room (CURRENT, AVAILABLE, VISITED, LOCKED).
     * Applies the appropriate CSS styles
     * and overlays.
     *
     */
    public void setRoomState() throws IllegalStateException {
        this.getStyleClass().removeAll("current", "available", "visited", "locked");

        RoomState roomState = this.room.getState();
        this.getStyleClass().add(roomState.toString().toLowerCase());

        // Configurer l'overlay selon l'état
        switch (roomState) {
            case RoomState.AVAILABLE -> {
                this.stateLabel.setText("");
                this.stateOverlay.setVisible(true);
                break;
            }
            case RoomState.VISITED -> {
                this.stateLabel.setText("✓");
                this.stateOverlay.setVisible(true);
                break;
            }
            case RoomState.LOCKED -> {
                this.stateLabel.setText("");
                this.stateOverlay.setVisible(false);
                break;
            }
            default -> {
                throw new IllegalStateException("Unexpected room state: " + roomState);
            }
        }

        this.setDisable(roomState == RoomState.LOCKED);
    }

    /**
     * Registers a listener to receive callbacks for room interactions.
     *
     * @param listener
     *                 The listener to be notified of room events
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onClick(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) { // left click
            return;
        }

        if (!this.isDisabled()) {
            this.listener.onRoomClicked(this.room.getPosition());
        }
    }

    /**
     * Callback interface for room node interactions. Dispatches room events to the
     * controller exclusively through this
     * interface.
     */
    public interface Listener {

        void onRoomClicked(RoomPosition position);
    }

    private Image loadImage(String imagePath) throws IllegalStateException {
        URL imageUrl = this.getClass().getResource(imagePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing required image resource: " + imagePath);
        }
        return new Image(imageUrl.toExternalForm());
    }
}
