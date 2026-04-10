package ulb.models.tower.room;

public final class EmptyRoom implements Room {
    public EmptyRoom() {

    }

    @Override
    public boolean isCompleted() {
        return false;
    }
}
