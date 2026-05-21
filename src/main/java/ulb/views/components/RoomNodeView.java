package ulb.views.components;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import ulb.Configuration;
import ulb.common.dto.display.RoomDisplayDTO;
import ulb.models.tower.room.Room;

/**
 * Custom component for a single tower room. Loads {@code Room.fxml} via {@code fx:root}, applies CSS classes for type
 * and state, and fires a click event only when the room is {@link RoomDisplayDTO.RoomState#AVAILABLE}.
 */
public class RoomNodeView extends StackPane {

    private static final Image BASE_TOKEN = loadImage("base.png");
    private static final Image ICON_COMBAT = loadImage("combat.png");
    private static final Image ICON_BOSS = loadImage("boss.png");
    private static final Image ICON_REWARD = loadImage("reward.png");

    @FXML
    private ImageView roomToken;
    @FXML
    private ImageView roomTypeIcon;

    private final Room roomRef;
    private final RoomDisplayDTO.RoomState state;
    private Listener listener;

    public RoomNodeView(RoomDisplayDTO dto) {
        FXMLLoader loader = new FXMLLoader(RoomNodeView.class.getResource(Configuration.Paths.Fxml.COMPONENT_ROOM));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load RoomNode component", e);
        }

        this.roomRef = dto.roomRef();
        this.state = dto.state();

        this.getStyleClass().add(dto.type().cssClass());
        this.getStyleClass().add(dto.state().cssClass());

        this.roomToken.setImage(BASE_TOKEN);
        Image icon = iconFor(dto.type());
        if (icon != null) {
            this.roomTypeIcon.setImage(icon);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onClick() {
        if (this.state != RoomDisplayDTO.RoomState.AVAILABLE) {
            return;
        }
        if (this.listener != null) {
            this.listener.onRoomClicked(this.roomRef);
        }
    }

    private static Image iconFor(RoomDisplayDTO.RoomType type) {
        return switch (type) {
            case COMBAT -> ICON_COMBAT;
            case BOSS -> ICON_BOSS;
            case REWARD -> ICON_REWARD;
            default -> null;
        };
    }

    private static Image loadImage(String filename) {
        InputStream stream = RoomNodeView.class.getResourceAsStream(Configuration.Paths.ROOM_BASE_PATH + filename);
        if (stream == null) {
            return null;
        }
        return new Image(stream);
    }

    public interface Listener {
        void onRoomClicked(Room room);
    }
}
