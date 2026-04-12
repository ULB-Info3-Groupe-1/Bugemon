package ulb.views;

import java.util.ArrayList;
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
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import ulb.views.components.RoomNodeView;

/**
 * View for the map of a NO Tower floor. Displays the layout of rooms and connections, and allows the player to click on
 * rooms.
 */
public class FloorMapView extends View {
    private static final String FXML_PATH = "/fxml/FloorMap.fxml";

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
    private VBox actionButtonsContainer;

    @FXML
    private ImageView playerIcon;

    private RoomNodeView currentRoom;
    private Listener listener;
    private List<RoomNodeView> roomNodes = new ArrayList<>();

    @Override
    public String getPath() {
        return FXML_PATH;
    }

    @Override
    public void refresh() {
        // The map is entirely dynamic and controlled by the controller, so no static data to refresh here. All updates
        // happen through explicit methods
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
    private void onBackToMainMenuClicked() {
        if (this.listener != null) {
            this.listener.onBackToMainMenu();
        }
    }

    /**
     * Sets the floor number displayed in the header.
     *
     * @param floorNumber
     *            The floor number (1-9)
     */
    public void setFloorNumber(int floorNumber) {
        this.floorNumberLabel.setText("Etage " + floorNumber);
    }

    /**
     * Sets the text of the instructions label.
     *
     * @param instructions
     *            The instructions text to display
     */
    public void setInstructions(String instructions) {
        this.instructionsLabel.setText(instructions);
    }

    public void setupPlayer(RoomNodeView startRoom, String imagePath) {
        if (this.playerIcon == null) {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            this.playerIcon = new ImageView(img);

            this.playerIcon.setFitWidth(70);
            this.playerIcon.setFitHeight(70);
            this.playerIcon.setPreserveRatio(true);

            this.innerMapPane.getChildren().add(this.playerIcon);
        }

        this.setPlayerPosition(startRoom);
    }

    public void setPlayerPosition(RoomNodeView room) {
        this.currentRoom = room;

        if (this.playerIcon == null || room == null) {
            return;
        }

        double playerX = room.getLayoutX() + (ROOM_WIDTH / 2) - (this.playerIcon.getFitWidth() / 2) + PLAYER_OFFSET_X;
        double playerY = room.getLayoutY() + (ROOM_HEIGHT / 2) - (this.playerIcon.getFitHeight() / 2) + PLAYER_OFFSET_Y;

        this.playerIcon.setLayoutX(playerX);
        this.playerIcon.setLayoutY(playerY);

        this.playerIcon.toFront();
    }

    public void animatePlayerTo(RoomNodeView room) {
        if (this.playerIcon == null || room == null) {
            return;
        }
        double targetX = room.getLayoutX() + (ROOM_WIDTH / 2) - (this.playerIcon.getFitWidth() / 2) + PLAYER_OFFSET_X;
        double targetY = room.getLayoutY() + (ROOM_HEIGHT / 2) - (this.playerIcon.getFitHeight() / 2) + PLAYER_OFFSET_Y;

        this.playerIcon.toFront();

        Timeline timeline = new Timeline();
        KeyValue keyValueX = new KeyValue(this.playerIcon.layoutXProperty(), targetX);
        KeyValue keyValueY = new KeyValue(this.playerIcon.layoutYProperty(), targetY);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(400), keyValueX, keyValueY);
        timeline.getKeyFrames().add(keyFrame);

        timeline.setOnFinished(event -> this.currentRoom = room);
        timeline.play();
    }

    /**
     * Adds a room node to the map. The x,y position is calculated automatically from row/col.
     *
     * @param roomNode
     *            The RoomNodeView component to add (must have row/col already set via setPosition)
     */
    public void addRoomNode(RoomNodeView roomNode) {
        // Calculate x,y position from row/col
        double x = this.calculateXPosition(roomNode.getCol());
        double y = this.calculateYPosition(roomNode.getRow());

        roomNode.setLayoutX(x);
        roomNode.setLayoutY(y);

        // Register listener to propagate events to FloorMapView's listener
        roomNode.setListener((row, col) -> {
            if (this.listener != null) {
                this.listener.onRoomClicked(roomNode);
            }
        });

        this.roomNodes.add(roomNode);
        this.innerMapPane.getChildren().add(roomNode);
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

    /**
     * Centers the map within the container by repositioning all rooms relative to their minimum row/col and sizing the
     * inner pane accordingly. The StackPane will automatically center the inner Pane.
     */
    public void centerMap() {
        if (this.roomNodes.isEmpty()) {
            return;
        }

        // Find bounds of all rooms
        int minRow = this.roomNodes.stream().mapToInt(RoomNodeView::getRow).min().orElse(0);
        int maxRow = this.roomNodes.stream().mapToInt(RoomNodeView::getRow).max().orElse(0);
        int minCol = this.roomNodes.stream().mapToInt(RoomNodeView::getCol).min().orElse(0);
        int maxCol = this.roomNodes.stream().mapToInt(RoomNodeView::getCol).max().orElse(0);

        // Reposition all rooms relative to the minimum row/col
        for (RoomNodeView roomNode : this.roomNodes) {
            double x = (roomNode.getCol() - minCol) * (ROOM_WIDTH + HORIZONTAL_SPACING);
            double y = (roomNode.getRow() - minRow) * (ROOM_HEIGHT + VERTICAL_SPACING);
            roomNode.setLayoutX(x);
            roomNode.setLayoutY(y);
        }

        // Calculate and set the inner pane size to fit all rooms
        // Formula: spacing between rooms + last room width (no spacing after last room)
        double paneWidth = (maxCol - minCol) * (ROOM_WIDTH + HORIZONTAL_SPACING) + ROOM_WIDTH;
        double paneHeight = (maxRow - minRow) * (ROOM_HEIGHT + VERTICAL_SPACING) + ROOM_HEIGHT;
        this.innerMapPane.setPrefSize(paneWidth, paneHeight);
        this.innerMapPane.setMinSize(paneWidth, paneHeight);
        this.innerMapPane.setMaxSize(paneWidth, paneHeight);
        this.innerMapPane.setMaxSize(paneWidth, paneHeight);

        if (this.currentRoom != null && this.playerIcon != null) {
            this.setPlayerPosition(this.currentRoom);
        }

    }

    /**
     * Adds a connection line between two RoomNodeViews.
     *
     * @param source
     *            Source room
     * @param target
     *            Target room
     */
    public void addConnectionBetweenRooms(RoomNodeView source, RoomNodeView target) {
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

    /**
     * Clears the map by removing all room nodes and connections.
     */
    public void clearMap() {
        this.roomNodes.clear();
        this.innerMapPane.getChildren().removeIf(node -> node != this.playerIcon);
    }

    /**
     * Retrieves the map container (for direct access if necessary).
     *
     * @return The StackPane container of the map
     */
    public StackPane getMapContainer() {
        return this.mapContainer;
    }

    /**
     * Retrieves a RoomNodeView at a specific position in the grid. Iterates through the children of the mapContainer to
     * find the corresponding node.
     *
     * @param row
     *            The row position to search for
     * @param col
     *            The column position to search for
     * @return The RoomNodeView found, or null if none
     */
    public RoomNodeView getRoomNodeAt(int row, int col) {
        return this.roomNodes.stream().filter(roomNode -> roomNode.getRow() == row && roomNode.getCol() == col)
                .findFirst().orElse(null);
    }

    /**
     * Callback interface for floor map interactions. Dispatches map events to the controller exclusively through this
     * interface.
     */
    public interface Listener {
        void onRoomClicked(RoomNodeView roomNode);

        void onBackToMainMenu();
    }
}
