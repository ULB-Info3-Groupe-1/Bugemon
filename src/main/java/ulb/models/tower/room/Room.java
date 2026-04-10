package ulb.models.tower.room;

public sealed interface Room permits CombatRoom, RewardRoom, EmptyRoom {
    boolean isCompleted();
}
