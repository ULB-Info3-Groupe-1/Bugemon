package ulb.common;

public enum RoomType {
    START,
    COMBAT,
    BOSS,
    REWARD,
    EMPTY;

    public String cssClass() {
        return "room-" + this.name().toLowerCase();
    }
}
