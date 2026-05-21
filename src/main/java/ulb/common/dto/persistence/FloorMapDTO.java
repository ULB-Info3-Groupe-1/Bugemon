package ulb.common.dto.persistence;

import java.util.List;

import ulb.models.tower.FloorMap.RoomPosition;

public record FloorMapDTO(int floor, List<RoomPosition> visitedRoomsPosition) {

}
