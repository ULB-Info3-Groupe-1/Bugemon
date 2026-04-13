package ulb.models.tower.room;

public final class EmptyRoom implements Room {
    public EmptyRoom() {

    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void visit(RoomVisitor roomVisitor) {
        roomVisitor.visitEmptyRoom(this);
    }

    @Override
    public RoomType getType() {
        return RoomType.EMPTY;
    }
}
