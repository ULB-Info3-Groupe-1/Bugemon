package ulb.models.tower.room;

import ulb.controllers.combat.TowerController;

public final class BonusRoom implements Room {
    public BonusRoom() {
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public void visit(TowerController controller) {
        controller.handleBonusRoom(this);
    }

    @Override
    public RoomType getType() {
        return RoomType.BONUS;
    }
}
