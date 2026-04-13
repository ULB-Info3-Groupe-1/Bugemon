package ulb.models.tower.room;

public interface RoomVisitor {

    public void visitCombatRoom(CombatRoom combatRoom);

    public void visitRewardRoom(RewardRoom rewardRoom);

    public void visitEmptyRoom(EmptyRoom emptyRoom);
}
