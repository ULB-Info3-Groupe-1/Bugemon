package bugemon.common.dto.display;

import bugemon.common.RoomState;
import bugemon.common.RoomType;

/**
 * Display-only snapshot of a single room. Rooms are identified by their {@code (x, y)} grid position.
 */
public record RoomDisplayDTO(int x, int y, RoomType type, RoomState state) {
}
