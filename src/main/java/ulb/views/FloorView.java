package ulb.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import ulb.Configuration;
import ulb.models.tower.FloorNode;
import ulb.views.components.RoomView;

/**
 * View for the map of a Tower floor. Displays the layout of rooms and connections, and allows the player to click on
 * rooms.
 */
public class FloorView extends View {
    private static final String FXML_PATH = "/fxml/Floor.fxml";
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

    private Map<FloorNode, RoomView> roomNodesByFloorNode;

    @FXML
    public void initialize() {
        this.initPlayerIcon(PLAYER_ICON_PATH);
    }

    @Override
    public String getPath() {
        return FXML_PATH;
    }

    @Override
    public void refresh() {
        // The map is entirely dynamic and controlled by the controller, so no
        // static data to refresh here. All updates happen through explicit methods
        // (setFloorNumber, addRoomNode, etc.)
    }

    /**
     * Registers a listener to receive callbacks for floor map interactions.
     *
     * @param listener
     *            The listener to be notified of map events
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
        this.floorNumberLabel.setText(Configuration.Ui.FLOOR_PREFIX + floorNumber);
    }

    public void setInstruction() {
        this.instructionsLabel.setText("Cliquez sur une salle disponible pour continuer votre ascension");
    }

    private void initPlayerIcon(String imagePath) {
        Image img = new Image(FloorView.class.getResourceAsStream(imagePath));
        this.playerIcon = new ImageView(img);

        this.playerIcon.setFitWidth(70);
        this.playerIcon.setFitHeight(70);
        this.playerIcon.setPreserveRatio(true);

        this.innerMapPane.getChildren().add(this.playerIcon);
    }

    public void setPlayerPosition(FloorNode node) {
        RoomView roomView = this.roomNodesByFloorNode.get(node);
        if (roomView == null) {
            return;
        }
        double playerX = roomView.getLayoutX() + (ROOM_WIDTH / 2) - (this.playerIcon.getFitWidth() / 2)
                + PLAYER_OFFSET_X;
        double playerY = roomView.getLayoutY() + (ROOM_HEIGHT / 2) - (this.playerIcon.getFitHeight() / 2)
                + PLAYER_OFFSET_Y;

        this.playerIcon.setLayoutX(playerX);
        this.playerIcon.setLayoutY(playerY);

        this.playerIcon.toFront();
    }

    public void animatePlayerTo(FloorNode node) {
        RoomView roomView = this.roomNodesByFloorNode.get(node);
        if (roomView == null) {
            return;
        }
        double targetX = roomView.getLayoutX() + (ROOM_WIDTH / 2) - (this.playerIcon.getFitWidth() / 2)
                + PLAYER_OFFSET_X;
        double targetY = roomView.getLayoutY() + (ROOM_HEIGHT / 2) - (this.playerIcon.getFitHeight() / 2)
                + PLAYER_OFFSET_Y;

        this.playerIcon.toFront();

        Timeline timeline = new Timeline();
        KeyValue keyValueX = new KeyValue(this.playerIcon.layoutXProperty(), targetX);
        KeyValue keyValueY = new KeyValue(this.playerIcon.layoutYProperty(), targetY);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(400), keyValueX, keyValueY);
        timeline.getKeyFrames().add(keyFrame);

        timeline.play();
    }

    public void setFloorNodes(List<FloorNode> floorNodes) {
        this.clearMap();

        this.roomNodesByFloorNode = new HashMap<>();
        for (FloorNode node : floorNodes) {
            this.addFloorNode(node);
        }

        this.centerMap();

        // Setup connections after centerMap to use final pixel positions
        for (Map.Entry<FloorNode, RoomView> entry : this.roomNodesByFloorNode.entrySet()) {
            RoomView roomView = entry.getValue();
            for (FloorNode connectedNode : entry.getKey().getChildren()) {
                RoomView connectedRoomView = this.roomNodesByFloorNode.get(connectedNode);
                if (connectedRoomView != null) {
                    this.addConnectionBetweenRooms(roomView, connectedRoomView);
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
        roomView.setListener(position -> FloorView.this.listener.onRoomClicked(floorNode));

        this.roomNodesByFloorNode.put(floorNode, roomView);
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

    public void refreshRoomStates() {
        for (RoomView roomView : this.roomNodesByFloorNode.values()) {
            roomView.setRoomState();
        }
    }

    public void clearMap() {
        this.innerMapPane.getChildren().removeIf(node -> node != this.playerIcon);
    }

    private void centerMap() {
        if (this.roomNodesByFloorNode.isEmpty()) {
            return;
        }

        // Find bounds of all rooms
        int minCol = this.roomNodesByFloorNode.keySet().stream().mapToInt(FloorNode::getX).min().orElse(0);
        int maxCol = this.roomNodesByFloorNode.keySet().stream().mapToInt(FloorNode::getX).max().orElse(0);
        int minRow = this.roomNodesByFloorNode.keySet().stream().mapToInt(FloorNode::getY).min().orElse(0);
        int maxRow = this.roomNodesByFloorNode.keySet().stream().mapToInt(FloorNode::getY).max().orElse(0);

        // Reposition all rooms relative to the minimum row/col
        for (Map.Entry<FloorNode, RoomView> entry : this.roomNodesByFloorNode.entrySet()) {
            FloorNode node = entry.getKey();
            RoomView roomView = entry.getValue();
            double x = (node.getX() - minCol) * (ROOM_WIDTH + HORIZONTAL_SPACING);
            double y = (node.getY() - minRow) * (ROOM_HEIGHT + VERTICAL_SPACING);
            roomView.setLayoutX(x);
            roomView.setLayoutY(y);
        }

        // Calculate and set the inner pane size to fit all rooms
        // Formula: spacing between rooms + last room width (no spacing after last
        // room)
        double paneWidth = (maxCol - minCol) * (ROOM_WIDTH + HORIZONTAL_SPACING) + ROOM_WIDTH;
        double paneHeight = (maxRow - minRow) * (ROOM_HEIGHT + VERTICAL_SPACING) + ROOM_HEIGHT;
        this.innerMapPane.setPrefSize(paneWidth, paneHeight);
        this.innerMapPane.setMinSize(paneWidth, paneHeight);
        this.innerMapPane.setMaxSize(paneWidth, paneHeight);
    }

    /**
     * Callback interface for floor map interactions. Dispatches map events to the controller exclusively through this
     * interface.
     */
    public interface Listener {
        void onRoomClicked(FloorNode node);

        void onReturnToMainMenu();
    }
}
