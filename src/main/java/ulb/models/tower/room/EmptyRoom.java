package ulb.models.tower.room;

public final class EmptyRoom extends Room {

    @Override
    public void visit(RoomVisitor roomVisitor) {
        roomVisitor.visitEmptyRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return RoomType.EMPTY;
    }
}
