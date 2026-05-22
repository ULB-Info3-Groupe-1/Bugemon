package ulb.common.dto.display;

import ulb.common.RoomState;
import ulb.common.RoomType;

/**
 * Display-only snapshot of a single room. Rooms are identified by their {@code (x, y)} grid position.
 */
public record RoomDisplayDTO(int x, int y, RoomType type, RoomState state) {
}
