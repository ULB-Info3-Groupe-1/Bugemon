package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import ulb.Configuration;
import ulb.common.dto.display.ConnectionDisplayDTO;
import ulb.common.dto.display.FloorDisplayDTO;
import ulb.common.dto.display.RoomDisplayDTO;
import ulb.models.tower.room.Room;
import ulb.views.components.RoomNodeView;

public class FloorView extends View {

    private static final int CELL_SIZE = 110;
    private static final int ROOM_SIZE = 75;

    @FXML
    private Label floorNumberLabel;
    @FXML
    private Pane innerMapPane;

    private Listener listener;
    private int currentFloor;
    private FloorDisplayDTO floorDTO;

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
    }

    private void resizePane() {
        int maxRow = this.floorDTO.rooms().stream().mapToInt(RoomDisplayDTO::row).max().orElse(0);
        int maxCol = this.floorDTO.rooms().stream().mapToInt(RoomDisplayDTO::col).max().orElse(0);
        this.innerMapPane.setPrefSize((maxCol + 1) * CELL_SIZE, (maxRow + 1) * CELL_SIZE);
    }

    private void drawConnections() {
        for (ConnectionDisplayDTO conn : this.floorDTO.connections()) {
            Line line = new Line(centerX(conn.fromCol()), centerY(conn.fromRow()), centerX(conn.toCol()),
                    centerY(conn.toRow()));
            line.getStyleClass().add("room-connection");
            this.innerMapPane.getChildren().add(line);
        }
    }

    private void drawRooms() {
        for (RoomDisplayDTO dto : this.floorDTO.rooms()) {
            RoomNodeView node = new RoomNodeView(dto);
            node.setLayoutX(topLeftX(dto.col()));
            node.setLayoutY(topLeftY(dto.row()));
            node.setListener(room -> this.listener.onRoomClicked(room));
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
        void onRoomClicked(Room room);

        void onReturnToMainMenu();
    }
}
