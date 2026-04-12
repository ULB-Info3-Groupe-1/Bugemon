package ulb.models.tower.room;

import ulb.controllers.combat.TowerController;

public sealed interface Room permits CombatRoom, RewardRoom, EmptyRoom {
    boolean isCompleted();

    void visit(TowerController controller);

    RoomType getType();
}
