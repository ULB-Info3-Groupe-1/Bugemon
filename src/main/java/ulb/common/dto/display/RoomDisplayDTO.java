package ulb.common.dto.display;

import ulb.common.RoomState;
import ulb.common.RoomType;

/**
 * Display-only snapshot of a single room. Rooms are identified by their
 * {@code (x, yl)} grid position — the
 * controller uses this to look up the domain object on click.
 */
public record RoomDisplayDTO(int x, int y, RoomType type, RoomState state) {
}
