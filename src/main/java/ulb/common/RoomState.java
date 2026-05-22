package ulb.common;

public enum RoomState {
    CURRENT,
    AVAILABLE,
    VISITED,
    VISITED_AVAILABLE,
    LOCKED;

    public String cssClass() {
        return this.name().toLowerCase();
    }
}
