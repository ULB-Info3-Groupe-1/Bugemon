package ulb.models.tower.room;

public final class EmptyRoom extends Room {

    @Override
    public void visitIfNotVisited(RoomVisitor roomVisitor) {
        if (this.isVisited()) {
            return;
        }
        roomVisitor.visitEmptyRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return RoomType.EMPTY;
    }
}
