package ulb.models.no_tower.room;

public sealed interface Room permits CombatRoom, RewardRoom, EmptyRoom {
}
