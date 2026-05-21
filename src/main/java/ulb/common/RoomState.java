package ulb.common;

public enum RoomState {
    CURRENT,
    AVAILABLE,
    VISITED,
    LOCKED;

    public String cssClass() {
        return this.name().toLowerCase();
    }
}
