package ulb.models.tower.room;

import ulb.controllers.combat.TowerController;

public final class EmptyRoom implements Room {
    public EmptyRoom() {

    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public void visit(TowerController controller) {
        controller.handleEmptyRoom(this);
    }

    @Override
    public RoomType getType() {
        return RoomType.EMPTY;
    }
}
