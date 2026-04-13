package ulb.models.tower.room;

public interface RoomVisitor {

    void visitCombatRoom(CombatRoom combatRoom);

    void visitRewardRoom(RewardRoom rewardRoom);

    void visitEmptyRoom(EmptyRoom emptyRoom);
}
