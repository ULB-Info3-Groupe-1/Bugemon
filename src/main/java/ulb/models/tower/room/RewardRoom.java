package ulb.models.tower.room;

import ulb.controllers.combat.TowerController;

public final class RewardRoom implements Room {
    public RewardRoom() {
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public void visit(TowerController controller) {
        controller.handleRewardRoom(this);
    }

    @Override
    public RoomType getType() {
        return RoomType.REWARD;
    }
}
