package bugemon.common.dto.persistence;

import java.util.List;

import bugemon.common.models.utils.Position;

/**
 * Serialisable snapshot of the player's progress through a single tower floor.
 *
 * @param floor
 *            the floor number (1-based)
 * @param visitedRoomsPosition
 *            grid positions of all rooms the player has already entered
 * @param currentRoomPosition
 *            grid position of the room the player is currently in
 */
public record FloorMapDTO(int floor, List<Position> visitedRoomsPosition, Position currentRoomPosition) {
}
