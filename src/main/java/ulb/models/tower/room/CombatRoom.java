package ulb.models.tower.room;

import ulb.models.combat.Combat;

public record CombatRoom(Combat combat, boolean isBoss) implements Room {
}
