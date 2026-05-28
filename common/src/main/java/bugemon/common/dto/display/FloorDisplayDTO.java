package bugemon.common.dto.display;

import java.util.List;

/**
 * Complete display snapshot of a tower floor: floor number, rooms and edges between them.
 */
public record FloorDisplayDTO(int floorNumber, List<RoomDisplayDTO> rooms, List<ConnectionDisplayDTO> connections) {
}
