package ulb.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import ulb.Configuration;
import ulb.common.RoomState;
import ulb.common.dto.display.ConnectionDisplayDTO;
import ulb.common.dto.display.FloorDisplayDTO;
import ulb.common.dto.display.RoomDisplayDTO;
import ulb.views.components.RoomNodeView;

/**
 * View for the tower floor map. Renders room nodes and connecting lines on an absolute-positioned {@link Pane} scaled
 * by a fixed cell grid, animates the player icon between rooms, and forwards click and navigation events through
 * {@link Listener}.
 */
public class FloorView extends View {

    private static final int CELL_SIZE = 110;
    private static final int ROOM_SIZE = 75;
    private static final double PLAYER_ICON_HEIGHT = 50.0;

    @FXML
    private Label floorNumberLabel;
    @FXML
    private Pane innerMapPane;

    private ImageView playerIcon;
    private double playerIconWidth;
    private Listener listener;
    private int currentFloor;
    private FloorDisplayDTO floorDTO;

    @FXML
    public void initialize() {
        var url = FloorView.class.getResource(Configuration.Paths.PLAYER_ICON);
        if (url != null) {
            Image img = new Image(url.toExternalForm(), 0, PLAYER_ICON_HEIGHT, true, false);

            this.playerIcon = new ImageView(img);
            this.playerIcon.setPreserveRatio(true);
            this.playerIconWidth = img.getWidth();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Stores the floor number and layout data that will be rendered on the next {@link #refresh()} call.
     *
     * @param floor
     *            the current floor index, displayed in the header label
     * @param dto
     *            the room and connection layout for the floor
     */
    public void setFloorState(int floor, FloorDisplayDTO dto) {
        this.currentFloor = floor;
        this.floorDTO = dto;
    }

    @FXML
    private void onReturnToMainMenuClicked() {
        this.listener.onReturnToMainMenu();
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.FLOOR_VIEW;
    }

    @Override
    public void refresh() {
        this.floorNumberLabel.setText("Étage " + this.currentFloor);
        this.innerMapPane.getChildren().clear();

        if (this.floorDTO == null) {
            return;
        }

        this.resizePane();
        this.drawConnections();
        this.drawRooms();

        this.innerMapPane.getChildren().add(this.playerIcon);
        this.playerIcon.toFront();
        this.floorDTO.rooms().stream().filter(r -> r.state() == RoomState.CURRENT).findFirst().ifPresent(r -> {
            this.playerIcon.setLayoutX(this.playerX(r.x()));
            this.playerIcon.setLayoutY(playerY(r.y()));
        });
    }

    /**
     * Smoothly moves the player icon to the specified grid cell over the configured animation duration, then invokes
     * the callback.
     *
     * @param x
     *            target column in the room grid
     * @param y
     *            target row in the room grid
     * @param onFinished
     *            called on the JavaFX thread after the animation completes
     */
    public void animatePlayerTo(int x, int y, Runnable onFinished) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(Configuration.Ui.FLOOR_MOVE_ANIMATION_MS),
                new KeyValue(this.playerIcon.layoutXProperty(), this.playerX(x)),
                new KeyValue(this.playerIcon.layoutYProperty(), playerY(y))));
        timeline.setOnFinished(e -> onFinished.run());
        timeline.play();
    }

    private double playerX(int col) {
        return centerX(col) - this.playerIconWidth / 2;
    }

    private static double playerY(int row) {
        return centerY(row) - PLAYER_ICON_HEIGHT / 2;
    }

    private void resizePane() {
        int maxX = this.floorDTO.rooms().stream().mapToInt(RoomDisplayDTO::x).max().orElse(0);
        int maxY = this.floorDTO.rooms().stream().mapToInt(RoomDisplayDTO::y).max().orElse(0);
        this.innerMapPane.setPrefSize((maxX + 1) * (double) CELL_SIZE, (maxY + 1) * (double) CELL_SIZE);
        this.innerMapPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    private void drawConnections() {
        for (ConnectionDisplayDTO conn : this.floorDTO.connections()) {
            Line line = new Line(centerX(conn.fromX()), centerY(conn.fromY()), centerX(conn.toX()),
                    centerY(conn.toY()));
            line.getStyleClass().add("room-connection");
            this.innerMapPane.getChildren().add(line);
        }
    }

    private void drawRooms() {
        for (RoomDisplayDTO dto : this.floorDTO.rooms()) {
            RoomNodeView node = new RoomNodeView(dto);
            node.setLayoutX(topLeftX(dto.x()));
            node.setLayoutY(topLeftY(dto.y()));
            node.setListener((row, col) -> this.listener.onRoomClicked(row, col));
            this.innerMapPane.getChildren().add(node);
        }
    }

    private static double centerX(int col) {
        return col * CELL_SIZE + CELL_SIZE / 2.0;
    }

    private static double centerY(int row) {
        return row * CELL_SIZE + CELL_SIZE / 2.0;
    }

    private static double topLeftX(int col) {
        return col * CELL_SIZE + (CELL_SIZE - ROOM_SIZE) / 2.0;
    }

    private static double topLeftY(int row) {
        return row * CELL_SIZE + (CELL_SIZE - ROOM_SIZE) / 2.0;
    }

    /** Callback interface for floor map user interactions. */
    public interface Listener {
        /**
         * Called when the player clicks a room node.
         *
         * @param row
         *            row (y) coordinate of the clicked room
         * @param col
         *            column (x) coordinate of the clicked room
         */
        void onRoomClicked(int row, int col);

        /** Called when the player chooses to return to the main menu. */
        void onReturnToMainMenu();
    }
}
