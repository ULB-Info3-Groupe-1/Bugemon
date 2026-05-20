package ulb.repositories.dto;

import java.util.List;

import ulb.models.tower.utils.Position;



public record FloorMapDTO(int floorNumber, List<Position> visitedRoomsPosition) {
    
}
