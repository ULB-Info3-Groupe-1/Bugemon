package ulb.views;

import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import ulb.models.tower.FloorNode;
import ulb.models.tower.room.Room;
import ulb.models.tower.room.Room.RoomPosition;
import ulb.views.components.RoomView;

/**
 * View for the map of a NO Tower floor. Displays the layout of rooms and
 * connections, and allows the player to click on
 * rooms.
 */
public class FloorView extends View {
    private static final String FXML_PATH = "/fxml/FloorMap.fxml";
    private static final String PLAYER_ICON_PATH = "/png/Trainer.png";

    // Constants for automatic positioning
    private static final double ROOM_WIDTH = 80.0;
    private static final double ROOM_HEIGHT = 80.0;
    private static final double HORIZONTAL_SPACING = 70.0;
    private static final double VERTICAL_SPACING = 70.0;
    private static final double MAP_OFFSET_X = 0.0;
    private static final double MAP_OFFSET_Y = 0.0;
    private static final double PLAYER_OFFSET_X = 12;
    private static final double PLAYER_OFFSET_Y = -7.0;

    @FXML
    private Label floorNumberLabel;

    @FXML
    private Label instructionsLabel;

    @FXML
    private StackPane mapContainer;

    @FXML
    private Pane innerMapPane;

    @FXML
    private ImageView playerIcon;

    private Listener listener;

    public void initialize() {
        initPlayerIcon(PLAYER_ICON_PATH);
    }

    @Override
    public String getPath() {
        return FXML_PATH;
    }

    @Override
    public void refresh() {
        // The map is entirely dynamic and controlled by the controller, so no static
        // data to refresh here. All updates
        // happen through explicit methods
        // (setFloorNumber, addRoomNode, etc.)
    }

    /**
     * Registers a listener to receive callbacks for floor map interactions.
     *
     * @param listener
     *                 The listener to be notified of map events
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @FXML
    private void onReturnToMainMenuClicked() {
        if (this.listener != null) {
            this.listener.onReturnToMainMenu();
        }
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumberLabel.setText("Étage " + floorNumber);
    }

    public void setInstruction() {
        this.instructionsLabel.setText("Cliquez sur une salle disponible pour continuer votre ascension");
    }

    private void initPlayerIcon(String imagePath) {
        Image img = new Image(getClass().getResourceAsStream(imagePath));
        this.playerIcon = new ImageView(img);

        this.playerIcon.setFitWidth(70);
        this.playerIcon.setFitHeight(70);
        this.playerIcon.setPreserveRatio(true);

        this.innerMapPane.getChildren().add(this.playerIcon);
    }

    public void setupPlayer(Room startRoom, String imagePath) {
        this.setPlayerPosition(startRoom.getPosition());
    }

    public void setPlayerPosition(RoomPosition position) {
        double playerX = position.col() + (ROOM_WIDTH / 2) - (this.playerIcon.getFitWidth() / 2) + PLAYER_OFFSET_X;
        double playerY = position.row() + (ROOM_HEIGHT / 2) - (this.playerIcon.getFitHeight() / 2) + PLAYER_OFFSET_Y;

        this.playerIcon.setLayoutX(playerX);
        this.playerIcon.setLayoutY(playerY);

        this.playerIcon.toFront();
    }

    public void animatePlayerTo(RoomPosition position) {
        double targetX = position.col() + (ROOM_WIDTH / 2) - (this.playerIcon.getFitWidth() / 2) + PLAYER_OFFSET_X;
        double targetY = position.row() + (ROOM_HEIGHT / 2) - (this.playerIcon.getFitHeight() / 2) + PLAYER_OFFSET_Y;

        this.playerIcon.toFront();

        Timeline timeline = new Timeline();
        KeyValue keyValueX = new KeyValue(this.playerIcon.layoutXProperty(), targetX);
        KeyValue keyValueY = new KeyValue(this.playerIcon.layoutYProperty(), targetY);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(400), keyValueX, keyValueY);
        timeline.getKeyFrames().add(keyFrame);

        timeline.play();
    }

    public void setFloorNodes(List<FloorNode> floorNodes) {
        for (FloorNode node : floorNodes) {
            this.addFloorNode(node);
        }

        for (FloorNode node : floorNodes) {
            for (FloorNode child : node.getChildren()) {
                RoomView target = this.roomNodesByFloorNode.get(child);
                if (target != null) {
                    this.view.addConnectionBetweenRooms(source, target);
                }
            }
        }
    }

    public void addFloorNode(FloorNode floorNode) {
        // Calculate x,y position from row/col
        double x = this.calculateXPosition(floorNode.getX());
        double y = this.calculateYPosition(floorNode.getY());

        RoomView roomView = new RoomView(floorNode);

        roomView.setLayoutX(x);
        roomView.setLayoutY(y);

        // Register listener to propagate events to FloorMapView's listener
        roomView.setListener(new RoomView.Listener() {
            @Override
            public void onRoomClicked(RoomPosition position) {
                FloorView.this.listener.onRoomClicked(floorNode);

            }
        });

        for (FloorNode child : floorNode.getChildren()) {
            this.addConnectionBetweenRooms(floorNode, child);

        }

        this.innerMapPane.getChildren().add(roomView);
    }

    /**
     * Calculates the X position in pixels from a column number.
     *
     * @param col
     *            Column number (0-indexed)
     * @return X position in pixels
     */
    private double calculateXPosition(int col) {
        return MAP_OFFSET_X + (col * (ROOM_WIDTH + HORIZONTAL_SPACING));
    }

    /**
     * Calculates the Y position in pixels from a row number.
     *
     * @param row
     *            Row number (0-indexed)
     * @return Y position in pixels
     */
    private double calculateYPosition(int row) {
        return MAP_OFFSET_Y + (row * (ROOM_HEIGHT + VERTICAL_SPACING));
    }

    public void addConnectionBetweenRooms(RoomView source, RoomView target) {
        // Calculate the center of each room
        double sourceX = source.getLayoutX() + ROOM_WIDTH / 2;
        double sourceY = source.getLayoutY() + ROOM_HEIGHT / 2;
        double targetX = target.getLayoutX() + ROOM_WIDTH / 2;
        double targetY = target.getLayoutY() + ROOM_HEIGHT / 2;

        Line connection = new Line(sourceX, sourceY, targetX, targetY);
        connection.getStyleClass().add("room-connection");
        connection.setStrokeWidth(3);

        // Add the line first so it's under the RoomNodes
        this.innerMapPane.getChildren().add(0, connection);
    }

    public void clearMap() {
        this.innerMapPane.getChildren().clear();
    }

    /**
     * Callback interface for floor map interactions. Dispatches map events to the
     * controller exclusively through this
     * interface.
     */
    public interface Listener {
        void onRoomClicked(FloorNode node);

        void onReturnToMainMenu();
    }
}
