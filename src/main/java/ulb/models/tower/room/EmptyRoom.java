package ulb.models.tower.room;

public final class EmptyRoom extends Room {

    @Override
    public void visitIfNotVisited(RoomVisitor roomVisitor) {
        System.out.println("Empty room visited");
        roomVisitor.visitEmptyRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return RoomType.EMPTY;
    }
}
