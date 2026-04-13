package ulb.models.tower.room;

public interface Room {
    boolean isCompleted();

    void visit(RoomVisitor roomVisitor);

    RoomType getType();
}
