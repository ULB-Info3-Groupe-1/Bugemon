package ulb.common.dto.display;

import ulb.models.tower.room.Room;

/**
 * Display-only snapshot of a single room. The {@code roomRef} is an opaque key passed back to the controller on click —
 * the view never calls methods on it.
 */
public record RoomDisplayDTO(int row, int col, RoomType type, RoomState state, Room roomRef) {

    public enum RoomType {
        START,
        COMBAT,
        BOSS,
        REWARD,
        EMPTY;

        public static RoomType from(Room.RoomType domain) {
            return switch (domain) {
                case START -> START;
                case COMBAT -> COMBAT;
                case BOSS -> BOSS;
                case REWARD -> REWARD;
                case EMPTY -> EMPTY;
            };
        }

        public String cssClass() {
            return "room-" + this.name().toLowerCase();
        }
    }

    public enum RoomState {
        CURRENT,
        AVAILABLE,
        VISITED,
        LOCKED;

        public String cssClass() {
            return this.name().toLowerCase();
        }
    }
}
