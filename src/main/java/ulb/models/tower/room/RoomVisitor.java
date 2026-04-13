package ulb.models.tower.room;

/** Visitor interface called when the player first enters a {@link Room}; each method handles one concrete room type. */
public interface RoomVisitor {

    void visitCombatRoom(CombatRoom combatRoom);

    void visitRewardRoom(RewardRoom rewardRoom);

    void visitEmptyRoom(EmptyRoom emptyRoom);
}
