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

public class FloorView extends View {

    private static final int CELL_SIZE = 110;
    private static final int ROOM_SIZE = 75;
    private static final double PLAYER_ICON_HEIGHT = 50.0;
    private static final String PLAYER_ICON_PATH = "/png/Trainer.png";

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
        Image img = new Image(FloorView.class.getResourceAsStream(PLAYER_ICON_PATH));
        this.playerIcon = new ImageView(img);
        this.playerIcon.setFitHeight(PLAYER_ICON_HEIGHT);
        this.playerIcon.setPreserveRatio(true);
        this.playerIconWidth = PLAYER_ICON_HEIGHT * (img.getWidth() / img.getHeight());
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

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
        this.innerMapPane.setPrefSize((maxX + 1) * CELL_SIZE, (maxY + 1) * CELL_SIZE);
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

    public interface Listener {
        void onRoomClicked(int row, int col);

        void onReturnToMainMenu();
    }
}
