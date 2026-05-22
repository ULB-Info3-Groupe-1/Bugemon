package ulb.common.dto.persistence;

import java.util.List;

import ulb.models.utils.Position;

public record FloorMapDTO(int floor, List<Position> visitedRoomsPosition, Position currentRoomPosition) {
}
